package io.github.alirostom1.smartshop.dto.response.product;

import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter @Setter
@SuperBuilder
public class ProductInternalResponse extends BaseInternalResponse {
    private String publicId;
    private String reference;
    private String name;
    private String category;
    private String description;
    private BigDecimal unitPrice;
    private Integer stock;
    private Integer reservedStock;

}
