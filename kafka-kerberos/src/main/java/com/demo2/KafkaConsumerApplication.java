package com.demo2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerApplication {
    private static final String TOPIC="test"; //
    private static final String CONFIG_DIR="C:\\Users\\Administrator\\Desktop\\pzwj\\";
    private static final String BOOTSTRAP_SERVERS="10.242.182.44:30626,10.242.182.44:30172,10.242.182.44:30865";

    public static void main(String[] args) {

        try {
            // 集群krb5.conf文件配置
            System.setProperty("java.security.krb5.conf", CONFIG_DIR + "krb5.conf");
            // 集群jaas.conf文件配置
            System.setProperty("java.security.auth.login.config", CONFIG_DIR + "jaas.conf");

            System.setProperty("zookeeper.server.principal", "zookeeper/tos_psq7qje");
              //  System.setProperty("sun.security.krb5.debug", "true");
//
//       // 集群kafka bootstrap-server
            Properties props = new Properties();
            props.put("bootstrap.servers", BOOTSTRAP_SERVERS);


//           //指定kafka消费者组 命名规则 账号名
            props.put("group.id", "default");
            props.put("enable.auto.commit", "false");
            props.put("auto.commit.interval.ms", "1000");// acks
//            props.put("auto.offset.reset", "earliest");// retries
            props.put("auto.offset.reset", "earliest");// retries
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            props.put("security.protocol", "SASL_PLAINTEXT");// security.protocol
            props.put("sasl.mechanism", "GSSAPI");// sasl.mechanism
            props.put("sasl.kerberos.service.name", "kafka");// sasl.kerberos.service.name
            props.put("sasl.kerberos.service.principal.instance", "tos_psq7qje");// security.protocol
            System.out.println("读取配置信息成功！");

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            System.out.println("开始创建");
            consumer.subscribe(Arrays.asList(TOPIC));
            System.err.println("----------开始消费------------");
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(10);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("offset =" + record.offset() + ",value =" + record.value());
                    System.err.println();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("读取配置信息失败！");
        }
    }

}
