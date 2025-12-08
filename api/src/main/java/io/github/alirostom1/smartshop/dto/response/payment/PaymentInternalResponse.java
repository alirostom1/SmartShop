package io.github.alirostom1.smartshop.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import io.github.alirostom1.smartshop.enums.PaymentMethod;
import io.github.alirostom1.smartshop.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuperBuilder
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInternalResponse extends BaseInternalResponse {
    private Integer paymentOrder;
    private BigDecimal amount;
    private String bank;
    private String reference;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private LocalDateTime depositDate;
}
