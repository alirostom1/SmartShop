package io.github.alirostom1.smartshop.dto.response.client;

import io.github.alirostom1.smartshop.enums.ClientTier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ClientStatsResponse {
    void setTotalOrders(Long totalOrders);
    void setTotalConfirmedOrders(Long totalConfirmedOrders);
    void setTotalSpent(BigDecimal totalSpent);

    void setFirstOrder(LocalDateTime firstOrderDate);
    void setLastOrder(LocalDateTime lastOrderDate);

    void setNextTier(ClientTier nextTier);
    void setAmountToNextTier(BigDecimal amountToNextTier);
    void setOrdersToNextTier(Long ordersToNextTier);
}
