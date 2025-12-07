package io.github.alirostom1.smartshop.dto.response.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.alirostom1.smartshop.enums.ClientTier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientWithStatsPublicResponse extends ClientPublicResponse implements ClientStatsResponse{
    private Long totalOrders;
    private Long totalConfirmedOrders;
    private BigDecimal totalSpent;

    private LocalDateTime firstOrder;
    private LocalDateTime lastOrder;

    private ClientTier nextTier;
    private BigDecimal amountToNextTier;
    private Long ordersToNextTier;
}
