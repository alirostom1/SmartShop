package io.github.alirostom1.smartshop.dto.response.order;

import io.github.alirostom1.smartshop.dto.response.payment.PaymentPublicResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderPublicResponse(
        String publicId,
        String status,
        BigDecimal totalTTC,
        String placedAt,
        BigDecimal remainingAmount,
        List<OrderItemPublicResponse> items,
        List<PaymentPublicResponse> payments
){
}
