A simple kafka-streams application to show how springboot can use to manage kafka-streams applicaiton. Typically kafka streams app are self-contained jar file that doesn't need spring. However, spring-kafka does provides a set of nice utilities to manage the lifecycle of the streams app. 

We take advantage of springkafka to implement the smart life cycle so it can detect the app start/stop and gracefully shutdown the the app and manage the state of the stream applicaion. This is particularly useful in a spring manage application

for more information, look at the 
```java
org.springframework.kafka.config.StreamsBuilderFactoryBean.start()/stop()
```
and passing in the config properties 

```java

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

```


This sample uses a few concepts in Kafkastream high level DSL provided to take input from a topic and calculate the running average, total cost, and total items for each product category.

Image you are running a e-commerce site where the product purchase items are forward to a kafka topic, for each product category, you want to know how many product are purchase, what the total cost, and what is the average purchase price for each category.



to run the app, need to have the kafka running, create a topic called **product.categories**

run the main app.

```KafkaStreamsSpringbootApplication```

and to send make product purchases to the topic, run the producer code in **SimpleProducer** to generate record to the topic.


finally, to code to do the aggregation is in here. **StreamsConfiguration** 

```java
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
```

