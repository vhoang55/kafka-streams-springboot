package vhoang.playground.kafkastreams.springboot;



import vhoang.playground.kafkastreams.springboot.domain.ProductCategoryAggregate;
import vhoang.playground.kafkastreams.springboot.domain.Product;
import vhoang.playground.kafkastreams.springboot.serdes.JsonDeserializer;
import vhoang.playground.kafkastreams.springboot.serdes.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

class AppCustomSerdes extends Serdes {

    static final class ProductSerde extends WrapperSerde<Product> {
        ProductSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    static Serde<Product> product() {
        ProductSerde serde = new ProductSerde();
        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("specific.class.name", Product.class);
        serde.configure(serdeConfigs, false);
        return serde;
    }

    static final class ProductCategoryAggSerde extends WrapperSerde<ProductCategoryAggregate> {
        ProductCategoryAggSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    static Serde<ProductCategoryAggregate> productCategoryAggregate() {
        ProductCategoryAggSerde serde = new ProductCategoryAggSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("specific.class.name", ProductCategoryAggregate.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
}
