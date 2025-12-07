package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.order.CreateOrderRequest;
import io.github.alirostom1.smartshop.dto.response.order.OrderInternalResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderPublicResponse;
import io.github.alirostom1.smartshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    //(ADMIN ONLY)
    OrderInternalResponse createOrder(CreateOrderRequest request);
    OrderInternalResponse confirmOrder(Long id);
    OrderInternalResponse cancelOrder(Long id);

    Page<OrderInternalResponse> getAllOrders(Pageable pageable);
    OrderInternalResponse getOrder(Long id);
    Page<OrderInternalResponse> getOrdersByClient(Long clientId,Pageable pageable);
    Page<OrderInternalResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    //(CLIENT ONLY)
    Page<OrderPublicResponse> getMyOrders(Long userId,Pageable pageable);
    OrderPublicResponse getMyOrder(Long userId,UUID publicId);

}
