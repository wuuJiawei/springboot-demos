package com.j.sample2;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Slf4j
public class KafkaKerberosProviderApp {


    public static KafkaProducer<String, String> PRODUCER = null;

    public static void main(String[] args) throws InterruptedException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS" );
        for (int i = 10; i < 20; i++){
            String s = i + "|" + sdf.format(new Date()) + "|" + UUID.randomUUID();
            send(s);
        }
    }

    public static KafkaProducer<String, String> getProducer() {
        if (PRODUCER == null) {
           synchronized (KafkaKerberosProviderApp.class) {
               System.setProperty("java.security.auth.login.config", ConfigHelper.getConfigDir() + "jaas.conf");
               System.setProperty("java.security.krb5.conf", ConfigHelper.getConfigDir() + "krb5.conf");
               System.setProperty("zookeeper.server.principal", "zookeeper/tos_psq7qje");
               //System.setProperty("sun.security.krb5.debug", "true");

               Properties props = new Properties();
               props.put("bootstrap.servers", ConfigHelper.getProperty("bootstrap-server"));
               props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
               props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
               // 集群kafka bootstrap-server
               props.put("security.protocol", "SASL_PLAINTEXT");// security.protocol
               props.put("sasl.mechanism", "GSSAPI");// sasl.mechanism
               props.put("sasl.kerberos.service.name", "kafka");// sasl.kerberos.service.name
               props.put("sasl.kerberos.service.principal.instance", "tos_psq7qje");// security.protocol
               props.put(ProducerConfig.ACKS_CONFIG, ConfigHelper.getProperty("acks", "ALL"));// acks
               props.put(ProducerConfig.RETRIES_CONFIG, 3);// retries
               PRODUCER = new KafkaProducer<>(props);
           }
        }
        return PRODUCER;
    }

    public static void send(String message) {
        send(message, null, null);
    }

    /**
     * 发送消息到kafka，并注册回调
     * @param message
     */
    public static void send(String message, Callback onSuccess, Callback onFailure) {
        String topic = ConfigHelper.getTopic();

        try{
            getProducer().send(new ProducerRecord<>(topic, message));
        } catch (Exception e) {
            log.error("Failed to send message to topic {}, error: {}", topic, e.getMessage());
            if (onFailure != null) {
                onFailure.onCompletion(null, e);
            }
        }

        KafkaProducer<String, String> producer = getProducer();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);

        // 异步发送并注册回调
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    log.error("Failed to send message to topic {}, error: {}", topic, exception.getMessage());
                    if (onFailure != null) {
                        onFailure.onCompletion(metadata, exception);
                    }
                } else {
                    log.info("Message sent to topic {} successfully, partition: {}, offset: {}", topic, metadata.partition(), metadata.offset());
                    if (onSuccess != null) {
                        onSuccess.onCompletion(metadata, null);
                    }
                }
            }
        });
        log.info("Message sent asynchronously: {}", message);
    }

}