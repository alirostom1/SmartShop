package io.github.alirostom1.smartshop.dto.response.order;

import io.github.alirostom1.smartshop.dto.response.product.ProductPublicResponse;

import java.math.BigDecimal;

public record OrderItemPublicResponse(
        String productName,
        String productReference,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
