package io.github.alirostom1.smartshop.dto.response.order;

import io.github.alirostom1.smartshop.dto.response.client.ClientInternalResponse;
import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@SuperBuilder
public class OrderInternalResponse extends BaseInternalResponse{
    private String publicId;
    private String status;
    private ClientInternalResponse client;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalTTC;
    private BigDecimal remainingAmount;
    private String promoCode;
    private List<OrderItemInternalResponse> items;
}
