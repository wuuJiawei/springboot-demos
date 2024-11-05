package com.j.sample2;

import ch.qos.logback.classic.Logger;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class MessageSender {

    public static void main(String[] args) {
        MessageSender messageSender = new MessageSender();
        messageSender.startSendingMessages();
    }

    public void startSendingMessages() {
        String addr = ConfigHelper.getSocketHost();
        int port = ConfigHelper.getSocketPort();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(addr, port);
                    OutputStream os = socket.getOutputStream();

                    String message = createMessage();

                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    log.info("Message sent: {}", message);

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000); // 3秒钟发送一次消息
    }

    private String getCurrentFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private String createMessage() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", getCurrentFormattedTime());
        map.put("message", UUID.randomUUID().toString());
        return new Gson().toJson(map);
//        return "hello world";
    }


}
