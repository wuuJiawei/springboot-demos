package com.demo1;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MhdsjzxApplication {
    private static final String TOPIC="v2x-data-spat";
    private static final String CONFIG_DIR= "." + File.separator + "pzwj" + File.separator;
//    private static final String BOOTSTRAP_SERVERS="10.242.182.44:30626,10.242.182.44:30172,10.242.182.44:30865";
    private static final String BOOTSTRAP_SERVERS="15.16.6.50:30865";

    public static void main(String[] args) throws InterruptedException {


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

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS" );

        for (int i = 10; i < 20; i++){

            String s = i + "|" + sdf.format(new Date()) + "|" + UUID.randomUUID().toString();
            System.out.println(s);
            try{
                producer.send(new ProducerRecord<>(TOPIC,s ));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception.........");
            } finally {
                Thread.sleep(1000);
            }
        }
    }
}