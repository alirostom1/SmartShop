package io.github.alirostom1.smartshop.dto.request.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
public class FilterProductsRequest {
    private String name;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStockOnly = false;
}
