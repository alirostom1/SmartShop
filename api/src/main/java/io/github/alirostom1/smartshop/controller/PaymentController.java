package io.github.alirostom1.smartshop.controller;

import io.github.alirostom1.smartshop.annotation.AuthZ;
import io.github.alirostom1.smartshop.dto.request.payment.BankTransferPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CashPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CheckPaymentRequest;
import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import io.github.alirostom1.smartshop.dto.response.payment.PaymentInternalResponse;
import io.github.alirostom1.smartshop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PostMapping("/cash")
    public ResponseEntity<ApiResponse<PaymentInternalResponse>> createCashPayment(
            @Valid @RequestBody CashPaymentRequest request,
            HttpServletRequest httpRequest) {

        PaymentInternalResponse response = paymentService.createCashPayment(request);
        ApiResponse<PaymentInternalResponse> apiResponse = ApiResponse.<PaymentInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Cash payment created successfully!")
                .data(response)
                .path(httpRequest.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<PaymentInternalResponse>> createCheckPayment(
            @Valid @RequestBody CheckPaymentRequest request,
            HttpServletRequest httpRequest) {

        PaymentInternalResponse response = paymentService.createCheckPayment(request);
        ApiResponse<PaymentInternalResponse> apiResponse = ApiResponse.<PaymentInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Check payment created successfully!")
                .data(response)
                .path(httpRequest.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PostMapping("/bank-transfer")
    public ResponseEntity<ApiResponse<PaymentInternalResponse>> createBankTransferPayment(
            @Valid @RequestBody BankTransferPaymentRequest request,
            HttpServletRequest httpRequest) {

        PaymentInternalResponse response = paymentService.createBankTransferPayment(request);
        ApiResponse<PaymentInternalResponse> apiResponse = ApiResponse.<PaymentInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Bank transfer payment created successfully!")
                .data(response)
                .path(httpRequest.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PostMapping("/{id}/deposit")
    public ResponseEntity<ApiResponse<PaymentInternalResponse>> depositPayment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        PaymentInternalResponse response = paymentService.depositPayment(id);
        ApiResponse<PaymentInternalResponse> apiResponse = ApiResponse.<PaymentInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Payment deposited successfully!")
                .data(response)
                .path(httpRequest.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @AuthZ("#user.role.name() == 'ADMIN'")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<PaymentInternalResponse>> rejectPayment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        PaymentInternalResponse response = paymentService.rejectPayment(id);
        ApiResponse<PaymentInternalResponse> apiResponse = ApiResponse.<PaymentInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Payment rejected successfully!")
                .data(response)
                .path(httpRequest.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}