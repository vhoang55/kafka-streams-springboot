package vhoang.playground.kafkastreams.springboot.producer;

import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vhoang.playground.kafkastreams.springboot.domain.Product;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;


public class SimpleProducer {

    private static final String topicName ="product.categories";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Producer<String, String> producer = new KafkaProducer<>(producerProps());
        fakeProductStreams().stream().forEach(product -> {
            try {
                producer.send(new ProducerRecord<>(topicName, product.getId(), objectMapper.writeValueAsString(product)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        producer.close();

    }

    private static Properties producerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    private static List<Product> fakeProductStreams() {
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("iPhone 11");
        product1.setDescription("Apple iphone 11");
        product1.setPrice(500D);
        product1.setCategory("ELECTRONIC");

        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Android 6");
        product2.setDescription("Google Android Phone");
        product2.setPrice(300D);
        product2.setCategory("ELECTRONIC");

        Product product3 = new Product();
        product3.setId("3");
        product3.setName("Walking Shoes");
        product3.setDescription("Nike walking shoes");
        product3.setPrice(100D);
        product3.setCategory("CLOTHING");

        Product product4 = new Product();
        product4.setId("4");
        product4.setName("Jacket");
        product4.setDescription("Man's Jacket");
        product4.setPrice(200D);
        product4.setCategory("CLOTHING");

        return List.of(product1, product2);
    }
}