package vhoang.playground.kafkastreams.springboot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryAggregate {

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }

    private Double totalPrice;
    private Integer productCount;
    private Double avgPrice;


    public ProductCategoryAggregate withTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public ProductCategoryAggregate withProductCount(Integer withProduct) {
        this.productCount = withProduct;
        return this;
    }

    public ProductCategoryAggregate withAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
        return this;
    }

    @Override
    public String toString() {
        return
                new ToStringBuilder(this)
                        .append("totalPrice", totalPrice)
                        .append("productCount", productCount)
                        .append("avgPrice", avgPrice)
                        .toString();
    }

}
