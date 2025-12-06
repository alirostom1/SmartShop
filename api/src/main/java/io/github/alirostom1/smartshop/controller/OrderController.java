package io.github.alirostom1.smartshop.controller;

import io.github.alirostom1.smartshop.annotation.AuthZ;
import io.github.alirostom1.smartshop.dto.request.order.CreateOrderRequest;
import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderInternalResponse;
import io.github.alirostom1.smartshop.dto.response.order.OrderPublicResponse;
import io.github.alirostom1.smartshop.enums.OrderStatus;
import io.github.alirostom1.smartshop.model.entity.User;
import io.github.alirostom1.smartshop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderInternalResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest
            ){
        OrderInternalResponse response = orderService.createOrder(request);
        ApiResponse<OrderInternalResponse> apiResponse = ApiResponse.<OrderInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Order placed successfully!")
                .data(response)
                .path(httpRequest.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    @AuthZ("#user.role.name() == 'ADMIN'")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderInternalResponse>> confirmOrder(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        OrderInternalResponse response = orderService.confirmOrder(id);
        ApiResponse<OrderInternalResponse> apiResponse = ApiResponse.<OrderInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Order confirmed Successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderInternalResponse>> cancelOrder(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        OrderInternalResponse response = orderService.cancelOrder(id);
        ApiResponse<OrderInternalResponse> apiResponse = ApiResponse.<OrderInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Order canceled Successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    @AuthZ("#user.role.name() == 'ADMIN'")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderInternalResponse>>> getAllOrders(
            @PageableDefault(page = 0, size = 5,sort = "createdAt",direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request
    ){
        Page<OrderInternalResponse> responses = orderService.getAllOrders(pageable);
        ApiResponse<Page<OrderInternalResponse>> apiResponse = ApiResponse.<Page<OrderInternalResponse>>builder()
                .success(true)
                .status(200)
                .message("Orders retrieved successfully!")
                .data(responses)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @AuthZ("#user.role.name() == 'ADMIN'")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderInternalResponse>> getOrder(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        OrderInternalResponse response = orderService.getOrder(id);
        ApiResponse<OrderInternalResponse> apiResponse = ApiResponse.<OrderInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Order retrieved successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @AuthZ("#user.role.name() == 'ADMIN'")
    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<Page<OrderInternalResponse>>> getOrdersByClient(
            @PageableDefault(page = 0, size = 5,sort = "createdAt",direction = Sort.Direction.DESC)
            Pageable pageable,
            @PathVariable("clientId") Long clientId,
            HttpServletRequest request
    ){
        Page<OrderInternalResponse> response = orderService.getOrdersByClient(clientId,pageable);
        ApiResponse<Page<OrderInternalResponse>> apiResponse = ApiResponse.<Page<OrderInternalResponse>>builder()
                .success(true)
                .status(200)
                .message("Orders retrieved successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @AuthZ("#user.role.name() == 'ADMIN'")
    @GetMapping("/{status}")
    public ResponseEntity<ApiResponse<Page<OrderInternalResponse>>> getOrdersByStatus(
            @PageableDefault(page = 0, size = 5,sort = "createdAt",direction = Sort.Direction.DESC)
            Pageable pageable,
            @PathVariable("status") OrderStatus status,
            HttpServletRequest request
    ){
        Page<OrderInternalResponse> response = orderService.getOrdersByStatus(status,pageable);
        ApiResponse<Page<OrderInternalResponse>> apiResponse = ApiResponse.<Page<OrderInternalResponse>>builder()
                .success(true)
                .status(200)
                .message("Orders retrieved successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<OrderPublicResponse>>> getClientOrders(
            @PageableDefault(page = 0, size = 5,sort = "createdAt",direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request,
            HttpSession session
    ){
        Page<OrderPublicResponse> response = orderService.getMyOrders(((User)session.getAttribute("user")).getId(),pageable);
        ApiResponse<Page<OrderPublicResponse>> apiResponse = ApiResponse.<Page<OrderPublicResponse>>builder()
                .success(true)
                .status(200)
                .message("Orders retrieved successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping("/me/{publicId}")
    public ResponseEntity<ApiResponse<OrderPublicResponse>> getClientOrder(
            @PathVariable("publicId") UUID publicId,
            HttpServletRequest request,
            HttpSession session
    ){
        OrderPublicResponse response = orderService.getMyOrder(((User)session.getAttribute("user")).getId(),publicId);
        ApiResponse<OrderPublicResponse> apiResponse = ApiResponse.<OrderPublicResponse>builder()
                .success(true)
                .status(200)
                .message("Order retrieved successfully!")
                .data(response)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
