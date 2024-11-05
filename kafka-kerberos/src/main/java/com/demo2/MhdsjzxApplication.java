package com.demo2;

import org.apache.kafka.clients.producer.ProducerConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MhdsjzxApplication {
    private static final String TOPIC="v2x-data-spat";
    private static final String CONFIG_DIR="C:\\Users\\Administrator\\Desktop\\pzwj\\";
//    private static final String CONFIG_DIR="./pzwj/";
    private static final String BOOTSTRAP_SERVERS="10.242.182.44:30626,10.242.182.44:30172,10.242.182.44:30865";

    public static void main(String[] args) throws InterruptedException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS" );
        for (int i = 10; i < 20; i++){
            String s = i + "|" + sdf.format(new Date()) + "|" + UUID.randomUUID().toString();
            send(s);
        }
    }

    public static KafkaProducer<String, String> PRODUCER = null;

    public static KafkaProducer<String, String> getProducer() {
        if (PRODUCER == null) {
            System.setProperty("java.security.auth.login.config",CONFIG_DIR + "jaas.conf");
            System.setProperty("java.security.krb5.conf",CONFIG_DIR + "krb5.conf");
            System.setProperty("zookeeper.server.principal", "zookeeper/tos_psq7qje");
            //System.setProperty("sun.security.krb5.debug", "true");

            Properties props = new Properties();
            props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
            props.put("key.serializer",
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer",
                    "org.apache.kafka.common.serialization.StringSerializer");
            // 集群kafka bootstrap-server
            props.put("security.protocol", "SASL_PLAINTEXT");// security.protocol
            props.put("sasl.mechanism", "GSSAPI");// sasl.mechanism
            props.put("sasl.kerberos.service.name", "kafka");// sasl.kerberos.service.name
            props.put("sasl.kerberos.service.principal.instance", "tos_psq7qje");// security.protocol
            props.put(ProducerConfig.ACKS_CONFIG, "all");// acks
            props.put(ProducerConfig.RETRIES_CONFIG, 3);// retries
            PRODUCER = new KafkaProducer<>(props);
        }
        return PRODUCER;
    }

    public static void send(String message) {
        System.out.println(message);

        try{
            getProducer().send(new ProducerRecord<>(TOPIC, message));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception.........");
        } finally {
            sleep(1000);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {}
    }
}