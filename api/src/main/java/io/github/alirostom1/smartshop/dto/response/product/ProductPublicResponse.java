package io.github.alirostom1.smartshop.dto.response.product;

import java.math.BigDecimal;

public record ProductPublicResponse(
        String publicId,
        String reference,
        String name,
        String category,
        String description,
        BigDecimal unitPrice,
        Integer stock
) {
}
