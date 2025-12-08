package io.github.alirostom1.smartshop.dto.response.payment;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.alirostom1.smartshop.enums.PaymentMethod;
import io.github.alirostom1.smartshop.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentPublicResponse (
        Integer paymentOrder,
        BigDecimal amount,
        String bank,
        String reference,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime paymentDate,
        LocalDateTime depositDate
){
}
