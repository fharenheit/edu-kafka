package io.datadynamics.edu.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ExactlyOnceProducer {
    public static void main(String[] args) {
        String bootstrapServers = "peter-kafka01.foo.bar:9092";
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        props.setProperty(ProducerConfig.RETRIES_CONFIG, "5");
        props.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "peter-transaction-01");

        Producer<String, String> producer = new KafkaProducer<>(props);

        producer.initTransactions();
        producer.beginTransaction();
        try {
            for (int i = 0; i < 1; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("peter-test05", "Apache Kafka is a distributed streaming platform - " + i);
                producer.send(record);
                producer.flush();
                System.out.println("Message sent successfully");
            }
        } catch (Exception e) {
            producer.abortTransaction();
            e.printStackTrace();
        } finally {
            producer.commitTransaction();
            producer.close();
        }
    }
}
