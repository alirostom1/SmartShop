package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.payment.BankTransferPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CashPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CheckPaymentRequest;
import io.github.alirostom1.smartshop.dto.response.payment.PaymentInternalResponse;

public interface PaymentService {
    PaymentInternalResponse createCashPayment(CashPaymentRequest request);
    PaymentInternalResponse createCheckPayment(CheckPaymentRequest request);
    PaymentInternalResponse createBankTransferPayment(BankTransferPaymentRequest request);

    PaymentInternalResponse depositPayment(Long id);
    PaymentInternalResponse rejectPayment(Long id);
}
