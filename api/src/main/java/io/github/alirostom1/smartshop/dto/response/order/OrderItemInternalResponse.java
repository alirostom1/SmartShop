package io.github.alirostom1.smartshop.dto.response.order;

import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductInternalResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter @Setter
@SuperBuilder
public class OrderItemInternalResponse extends BaseInternalResponse{
    private ProductInternalResponse product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
