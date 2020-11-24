package vhoang.playground.kafkastreams.springboot.domain;




import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    private String id;
    private String name;
    private String description;
    private String category;
    private Double price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return
                new ToStringBuilder(this)
                        .append("id", id)
                        .append("name", name)
                        .append("description", description)
                        .append("category", category)
                        .append("price", price)
                        .toString();
    }

}
