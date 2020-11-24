package vhoang.playground.kafkastreams.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class KafkaStreamsSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsSpringbootApplication.class, args);
	}

}
