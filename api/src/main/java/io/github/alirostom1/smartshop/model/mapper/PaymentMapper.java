package io.github.alirostom1.smartshop.model.mapper;

import io.github.alirostom1.smartshop.dto.request.payment.BankTransferPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CashPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CheckPaymentRequest;
import io.github.alirostom1.smartshop.dto.response.payment.PaymentInternalResponse;
import io.github.alirostom1.smartshop.dto.response.payment.PaymentPublicResponse;
import io.github.alirostom1.smartshop.model.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentPublicResponse paymentToPublicResponse(Payment payment);
    PaymentInternalResponse paymentToInternalResponse(Payment payment);

    Payment cashRequestToPayment(CashPaymentRequest request);
    Payment checkRequestToPayment(CheckPaymentRequest request);
    Payment bankTransferRequestToPayment(BankTransferPaymentRequest request);

}
