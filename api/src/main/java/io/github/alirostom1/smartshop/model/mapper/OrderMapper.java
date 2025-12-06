package io.github.alirostom1.smartshop.model.mapper;

import io.github.alirostom1.smartshop.dto.request.order.CreateOrderRequest;
import io.github.alirostom1.smartshop.dto.response.order.OrderInternalResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderItemInternalResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderItemPublicResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderPublicResponse;
import io.github.alirostom1.smartshop.model.entity.Order;
import io.github.alirostom1.smartshop.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = ClientMapper.class)
public interface OrderMapper {

    OrderItemPublicResponse orderItemToPublicResponse(OrderItem orderItem);
    OrderItemInternalResponse orderItemToInternalResponse(OrderItem orderItem);

    OrderPublicResponse orderToPublicResponse(Order order);
    OrderInternalResponse orderToInternalResponse(Order order);

    @Mapping(target = "items",ignore = true)
    Order orderRequestToEntity(CreateOrderRequest request);


}
