/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MhdsjzxApplication {
    private static final String TOPIC = "test";
    private static final String CONFIG_DIR = "D:\\Java相关\\KafkaJavaDemo\\KafkaJavaDemo\\pzwj\\";
    private static final String BOOTSTRAP_SERVERS = "15.16.6.50:30626,15.16.6.50:30172,15.16.6.50:30865";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.security.auth.login.config", CONFIG_DIR + "jaas.conf");
        System.setProperty("java.security.krb5.conf", CONFIG_DIR + "krb5.conf");
        System.setProperty("zookeeper.server.principal", "zookeeper/tos_psq7qje");
        System.setProperty("sun.security.krb5.debug", "true");
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "GSSAPI");
        props.put("sasl.kerberos.service.name", "kafka");
        props.put("sasl.kerberos.service.principal.instance", "tos_psq7qje");
        props.put("acks", "all");
        props.put("retries", 3);
        System.out.println("读取配置信息！");
        KafkaProducer producer = new KafkaProducer(props);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        System.out.println("认证成功");
        System.out.println("开始生产");
        for (int i = 10; i < 20; ++i) {
            String s2 = i + "|" + sdf.format(new Date()) + "|" + UUID.randomUUID().toString();
            System.out.println(s2);
            try {
                producer.send(new ProducerRecord(TOPIC, s2));
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("exception.........");
                continue;
            } finally {
                Thread.sleep(1000L);
            }
        }
    }
}

