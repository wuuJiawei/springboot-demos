package com.j.sample2;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
public class TcpServer {

    public static void main(String[] args) {
        TcpServer tcpServer = new TcpServer();
        tcpServer.listen();
    }

    public void listen() {
        String addr = ConfigHelper.getSocketHost();
        int port = ConfigHelper.getSocketPort();
        listen(addr, port);
    }

    public void listen(String addr, int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(addr));
            log.info("Server listening on {} : {}", addr, port);

            while (true) {
                Socket tcpClient = serverSocket.accept();
                log.info("Client connected");

                InputStream ns = tcpClient.getInputStream();
                byte[] buffer = new byte[100000];

                int num = ns.read(buffer);

                if (num != -1) {
                    byte[] packData = null;
                    try {
                        packData = Arrays.copyOf(buffer, num);
                    } catch (Exception ex) {
                        log.error("An error occurred while copying packet data: {}", ex.getMessage());
                        continue;
                    }
                    analysisPackData(packData);
                }
            }
        } catch (IOException e) {
            log.error("An error occurred: {}", e.getMessage());
        }
    }

    private void analysisPackData(byte[] packData) {
        String str = new String(packData, StandardCharsets.UTF_8);
        log.info("Received message: {}", str);
        String[] strs = str.split("I am a Separator");

        for (String item : strs) {
            if (StrUtil.isBlank(item)) {
                continue;
            }
            try {
                // Verify packet
                Gson gson = new Gson();
                JsonObject jsonObject = new JsonObject();
                try {
                    jsonObject = gson.fromJson(item, JsonObject.class);
                } catch (JsonSyntaxException e) {
                    log.error("Invalid JSON format: {}", item);
                    // Send to Kafka (original data)
                    try {
                        KafkaKerberosProviderApp.send(item);
                    } catch (Exception ex) {
                        log.error("An error occurred while sending packet data to Kafka: {}", ex.getMessage(), ex);
                    }
//                    continue;
                }
                log.info("Packet data: {}", jsonObject);

                // Send to database
                DatabaseHelper.insertData(item);
                log.info("Packet data sent to database successfully");

                // Send to Kafka
                KafkaKerberosProviderApp.send(jsonObject.toString(), (meta, exception)->{
                    // on success
                    log.info("Packet data sent to Kafka successfully");
                }, (meta, exception)->{
                    // on failure
                    log.error("An error occurred while sending packet data to Kafka: {}", exception.getMessage(), exception);
                });
            } catch (Exception ex) {
                // Record exception in file
                // Record item in file
                log.error("An error occurred while analyzing packet data: {}", ex.getMessage(), ex);
            }
        }
    }
}