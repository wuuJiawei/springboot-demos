/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerApplication {
    private static final String TOPIC = "test";
    private static final String CONFIG_DIR = "C:\\Users\\user22\\Desktop\\pudong\\kafka\\pzwj\\pzwj\\";
    private static final String BOOTSTRAP_SERVERS = "10.242.182.44:30626,10.242.182.44:30172,10.242.182.44:30865";

    public static void main(String[] args) {
        try {
            System.setProperty("java.security.krb5.conf", CONFIG_DIR + "krb5.conf");
            System.setProperty("java.security.auth.login.config", CONFIG_DIR + "jaas.conf");
            System.setProperty("zookeeper.server.principal", "zookeeper/tos_psq7qje");
            Properties props = new Properties();
            props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
            props.put("group.id", "default");
            props.put("enable.auto.commit", "false");
            props.put("auto.commit.interval.ms", "1000");
            props.put("auto.offset.reset", "earliest");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "GSSAPI");
            props.put("sasl.kerberos.service.name", "kafka");
            props.put("sasl.kerberos.service.principal.instance", "tos_psq7qje");
            System.out.println("读取配置信息成功！");
            KafkaConsumer consumer = new KafkaConsumer(props);
            System.out.println("开始创建");
            consumer.subscribe(Arrays.asList(TOPIC));
            System.err.println("----------开始消费------------");
            block2: while (true) {
                ConsumerRecords records2 = consumer.poll(10L);
                Iterator iterator2 = records2.iterator();
                while (true) {
                    if (!iterator2.hasNext()) continue block2;
                    ConsumerRecord record2 = (ConsumerRecord) iterator2.next();
                    System.out.println("offset =" + record2.offset() + ",value =" + (String)record2.value());
                    System.err.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("读取配置信息失败！");
            return;
        }
    }
}

