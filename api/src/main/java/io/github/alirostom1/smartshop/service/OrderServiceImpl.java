package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.annotation.AuthZ;
import io.github.alirostom1.smartshop.dto.request.order.CreateOrderRequest;
import io.github.alirostom1.smartshop.dto.request.order.OrderItemRequest;
import io.github.alirostom1.smartshop.dto.response.order.OrderInternalResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderPublicResponse;
import io.github.alirostom1.smartshop.enums.ClientTier;
import io.github.alirostom1.smartshop.enums.OrderStatus;
import io.github.alirostom1.smartshop.exception.AccessDeniedException;
import io.github.alirostom1.smartshop.exception.BusinessException;
import io.github.alirostom1.smartshop.exception.RessourceNotFoundException;
import io.github.alirostom1.smartshop.model.entity.Client;
import io.github.alirostom1.smartshop.model.entity.Order;
import io.github.alirostom1.smartshop.model.entity.OrderItem;
import io.github.alirostom1.smartshop.model.entity.Product;
import io.github.alirostom1.smartshop.model.mapper.OrderMapper;
import io.github.alirostom1.smartshop.repository.ClientRepository;
import io.github.alirostom1.smartshop.repository.OrderItemRepository;
import io.github.alirostom1.smartshop.repository.OrderRepository;
import io.github.alirostom1.smartshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Value("${app.tva}")
    private BigDecimal TVA_RATE;

    @Override
    public OrderInternalResponse createOrder(CreateOrderRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RessourceNotFoundException("Client not found!"));
        List<OrderItem> orderItems = extractOrderItems(request.getItems());
        Order order = buildOrder(orderItems,client,request);
        Order savedOrder = orderRepository.save(order);
        orderItems.forEach(item -> item.setOrder(savedOrder));
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(savedOrderItems);
        if(!checkStockAvailability(savedOrderItems)){
            savedOrder.setStatus(OrderStatus.REJECTED);
        }else{
            reserveStock(savedOrderItems);
        }
        return orderMapper.orderToInternalResponse(savedOrder);
    }

    @Override
    public OrderInternalResponse confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Order with id: " + id + " not found!"));
        if(order.getStatus() != OrderStatus.PENDING){
            throw new BusinessException("Only Pending orders can be confirmed!");
        }
        if(order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0){
            throw new BusinessException("Order must be fully paid before confirmation!");
        }
        order.getItems().forEach(oi -> {
            oi.getProduct().confirmReservation(oi.getQuantity());
        });
        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);
        recalculateClientTier(order.getClient());
        return orderMapper.orderToInternalResponse(savedOrder);
    }

    @Override
    public OrderInternalResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Order not found with id: " + id));
        if(order.getStatus() != OrderStatus.PENDING){
            throw new BusinessException("Only Pending orders can be cancelled!");
        }
        releaseReservedStock(order.getItems());
        order.setStatus(OrderStatus.CANCELED);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.orderToInternalResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderInternalResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::orderToInternalResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderInternalResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Order not found with id: " + id));
        return orderMapper.orderToInternalResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderInternalResponse> getOrdersByClient(Long clientId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByClient_Id(clientId,pageable);
        return orders.map(orderMapper::orderToInternalResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderInternalResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatus(status,pageable);
        return orders.map(orderMapper::orderToInternalResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderPublicResponse> getMyOrders(Long userId,Pageable pageable) {
        Page<Order> orders = orderRepository.findByClient_User_Id(userId,pageable);
        return orders.map(orderMapper::orderToPublicResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderPublicResponse getMyOrder(Long userId,UUID publicId) {
        Order order = orderRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RessourceNotFoundException("Order not found with id:  " + publicId));
        if(!order.getClient().getUser().getId().equals(userId)){
            throw new AccessDeniedException("You dont have access to this resource!");
        }
        return orderMapper.orderToPublicResponse(order);
    }


    private List<OrderItem> extractOrderItems(List<OrderItemRequest> items){
        return items.stream().map((orderItem) ->{
           Product product = productRepository.findById(orderItem.getProductId())
                   .orElseThrow(() -> new RessourceNotFoundException("Product with id: " + orderItem.getProductId() + " not found!"));
           OrderItem newOrderItem = new OrderItem();
           newOrderItem.setProduct(product);
           newOrderItem.setQuantity(orderItem.getQuantity());
           newOrderItem.setUnitPrice(product.getUnitPrice());
           newOrderItem.setTotalPrice(product.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
           return newOrderItem;
        }).toList();
    }

    private BigDecimal getLoyaltyDiscount(ClientTier clientTier){
        return clientTier.equals(ClientTier.BASIC) ? BigDecimal.ZERO
                : clientTier.equals(ClientTier.SILVER) ? BigDecimal.valueOf(0.05)
                : clientTier.equals(ClientTier.GOLD) ? BigDecimal.valueOf(0.1)
                : clientTier.equals(ClientTier.PLATINUM) ? BigDecimal.valueOf(0.15) : null;
    }
    private BigDecimal getLoyaltyDiscountThreshold(ClientTier clientTier){
        return clientTier.equals(ClientTier.BASIC) ? BigDecimal.ZERO
                : clientTier.equals(ClientTier.SILVER) ? BigDecimal.valueOf(500)
                : clientTier.equals(ClientTier.GOLD) ? BigDecimal.valueOf(800)
                : clientTier.equals(ClientTier.PLATINUM) ? BigDecimal.valueOf(1200)
                : BigDecimal.ZERO;
    }
    private BigDecimal validatePromoCode(String promoCode){
        if(!StringUtils.hasText(promoCode)){
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(0.05);
    }
    private BigDecimal calculateDiscount(BigDecimal subTotal,BigDecimal loyaltyDisountRate,BigDecimal promoDiscountRate){
        return subTotal.multiply(loyaltyDisountRate).add(subTotal.multiply(promoDiscountRate));
    }
    private Order buildOrder(List<OrderItem> orderItems,Client client,CreateOrderRequest request){
        BigDecimal subTotal = orderItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
        ClientTier tier = client.getTier();
        BigDecimal loyaltyDiscountRate = subTotal.compareTo(getLoyaltyDiscountThreshold(tier)) >= 0
                ? getLoyaltyDiscount(tier)
                : BigDecimal.ZERO;
        BigDecimal promoDiscountRate = validatePromoCode(request.getPromoCode());

        BigDecimal discountAmount = calculateDiscount(subTotal,loyaltyDiscountRate,promoDiscountRate);
        BigDecimal amountAfterDiscount = subTotal.subtract(discountAmount);
        BigDecimal taxAmount = amountAfterDiscount.multiply(TVA_RATE);
        BigDecimal totalTTC = amountAfterDiscount.add(taxAmount);

        Order order = orderMapper.orderRequestToEntity(request);
        order.setClient(client);
        order.setSubtotal(subTotal);
        order.setDiscountAmount(discountAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalTTC(totalTTC);
        order.setRemainingAmount(totalTTC);
        return order;
    }
    private boolean checkStockAvailability(List<OrderItem> items){
        return items.stream()
                .allMatch(item -> item.getProduct().getAvailableStock() >= item.getQuantity());
    }
    private void reserveStock(List<OrderItem> orderItems){
        orderItems.forEach(oi -> {
            Product product = productRepository.findById(oi.getProduct().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Product with id: " + oi.getProduct().getId() + " not found!"));
            product.reserveStock(oi.getQuantity());
            productRepository.save(product);
        });
    }
    private void releaseReservedStock(List<OrderItem> orderItems){
        orderItems.forEach(oi ->{
            Product product = productRepository.findById(oi.getProduct().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Product with id: " + oi.getProduct().getId() + " not found!"));
            product.releaseReservation(oi.getQuantity());
            productRepository.save(product);
        });
    }

    public void recalculateClientTier(Client client){
        Long totalOrders = orderRepository.countOrdersByClientAndStatus(client,OrderStatus.CONFIRMED);
        BigDecimal totalSpent = orderRepository.sumTotalTTCByClientAndStatus(client,OrderStatus.CONFIRMED);
        ClientTier newTier = totalOrders >= 20 || totalSpent.compareTo(BigDecimal.valueOf(15000)) >= 0 ? ClientTier.PLATINUM
                : totalOrders >= 10 || totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0 ? ClientTier.GOLD
                : totalOrders >= 3 || totalSpent.compareTo(BigDecimal.valueOf(1000)) >= 0 ? ClientTier.SILVER
                :ClientTier.BASIC;
        if(newTier != client.getTier()){
            client.setTier(newTier);
            clientRepository.save(client);
        }
    }
}
