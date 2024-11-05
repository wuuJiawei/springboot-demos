package com.j.sample1;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

@Slf4j
public class KafkaProducerApp {

    public static void main(String[] args) {
        String TOPIC = "test";

        // 设置Kafka生产者的属性
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092"); // Kafka服务器地址，根据实际情况修改
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 创建Kafka生产者实例
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // 发送十条消息
            for (int i = 0; i < 10; i++) {
                String message = "hello " + i;
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);

                // 异步发送并注册回调
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            log.error("Failed to send message to topic {}, error: {}", TOPIC, exception.getMessage());
                        } else {
                            log.info("Message sent to topic {} successfully, partition: {}, offset: {}",
                                    TOPIC, metadata.partition(), metadata.offset());
                        }
                    }
                });
                log.info("Message sent asynchronously: {}", message);
            }

            // 等待所有消息发送完成（非必须，取决于业务需求）
            // 注意：这会阻塞直到所有发送请求完成或超时，具体实现根据业务逻辑调整
             producer.flush();
        } catch (Exception e) {
            log.error("An unexpected error occurred during producer setup or teardown", e);
        }
    }

}
