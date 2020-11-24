package vhoang.playground.kafkastreams.springboot;

import vhoang.playground.kafkastreams.springboot.domain.ProductCategoryAggregate;
import vhoang.playground.kafkastreams.springboot.domain.Product;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StreamsConfiguration {

	//TODO; read this from application.properties using @Value("${}")
	public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
	public static final String STREAMS_APP_NAME = "kafkastreams-springboot-app";
	public static final String SOURCE_TOPIC_NAME = "product.categories";

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME)
	public StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration);
	}

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, STREAMS_APP_NAME);
		return new KafkaStreamsConfiguration(properties);
	}

	@Bean
	public KTable<String, ProductCategoryAggregate> kStream(StreamsBuilder streamsBuilder) {
		KStream<String, Product> purchasedStreams =
				streamsBuilder.stream(SOURCE_TOPIC_NAME, Consumed.with(AppCustomSerdes.String(), AppCustomSerdes.product()));

		KGroupedStream<String, Product> productsByCatGroupedStream =
				purchasedStreams.groupBy((k, v) -> v.getCategory(),
						Serialized.with(AppCustomSerdes.String(),
								AppCustomSerdes.product()));

		KTable<String, ProductCategoryAggregate> aggregateKTable = productsByCatGroupedStream.aggregate(
				//Initializer
				() -> new ProductCategoryAggregate()
						.withProductCount(0)
						.withTotalPrice(0D)
						.withAvgPrice(0D),
				//Aggregator
				(k, v, aggV) -> new ProductCategoryAggregate()
						.withProductCount(aggV.getProductCount() + 1)
						.withTotalPrice(aggV.getTotalPrice() + v.getPrice())
						.withAvgPrice((aggV.getTotalPrice() + v.getPrice()) / (aggV.getProductCount() + 1D)),
				//Serializer
				Materialized.<String, ProductCategoryAggregate, KeyValueStore<Bytes, byte[]>>as("product.category.agg.store")
						.withKeySerde(AppCustomSerdes.String())
						.withValueSerde(AppCustomSerdes.productCategoryAggregate())
		);

		aggregateKTable.toStream().foreach(
				(k, v) -> System.out.println("Key = " + k + " Value = " + v.toString()));

		return aggregateKTable;
	}

}
