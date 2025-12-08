package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.payment.BankTransferPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CashPaymentRequest;
import io.github.alirostom1.smartshop.dto.request.payment.CheckPaymentRequest;
import io.github.alirostom1.smartshop.dto.response.payment.PaymentInternalResponse;
import io.github.alirostom1.smartshop.enums.OrderStatus;
import io.github.alirostom1.smartshop.enums.PaymentMethod;
import io.github.alirostom1.smartshop.enums.PaymentStatus;
import io.github.alirostom1.smartshop.exception.BusinessException;
import io.github.alirostom1.smartshop.exception.RessourceNotFoundException;
import io.github.alirostom1.smartshop.model.entity.Order;
import io.github.alirostom1.smartshop.model.entity.Payment;
import io.github.alirostom1.smartshop.model.mapper.PaymentMapper;
import io.github.alirostom1.smartshop.repository.OrderRepository;
import io.github.alirostom1.smartshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final BigDecimal MAX_CASH_AMOUNT = BigDecimal.valueOf(20000L);

    @Override
    public PaymentInternalResponse createCashPayment(CashPaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RessourceNotFoundException("Order with id: " + request.getOrderId() + " not found!"));
        validateOrder(order,request.getAmount());
        validateCashAmount(request.getAmount());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String ref = "RC-" + uuid;

        Integer paymentOrder = getNextPaymentOrder(order.getId());

        Payment payment = paymentMapper.cashRequestToPayment(request);
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.DEPOSITED);
        payment.setOrder(order);
        payment.setReference(ref);
        payment.setPaymentOrder(paymentOrder);
        payment.setDepositDate(request.getPaymentDate());
        Payment savedPayment = paymentRepository.save(payment);
        subtractOrderRemainingAmount(order,request.getAmount());
        return paymentMapper.paymentToInternalResponse(savedPayment);
    }

    @Override
    public PaymentInternalResponse createCheckPayment(CheckPaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RessourceNotFoundException("Order with id: " + request.getOrderId() + " not found!"));
        if(paymentRepository.existsByReference(request.getReference())){
            throw new BusinessException("Payment with reference: " + request.getReference() + " already exists!");
        }
        validateOrder(order,request.getAmount());
        Integer paymentOrder = getNextPaymentOrder(order.getId());
        Payment payment = paymentMapper.checkRequestToPayment(request);
        payment.setOrder(order);
        payment.setMethod(PaymentMethod.CHECK);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentOrder(paymentOrder);
        Payment savedPayment = paymentRepository.save(payment);
        subtractOrderRemainingAmount(order,request.getAmount());
        return paymentMapper.paymentToInternalResponse(savedPayment);
    }

    @Override
    public PaymentInternalResponse createBankTransferPayment(BankTransferPaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RessourceNotFoundException("Order with id: " + request.getOrderId() + " not found!"));
        if(paymentRepository.existsByReference(request.getReference())){
            throw new BusinessException("Payment with reference: " + request.getReference() + " already exists!");
        }
        validateOrder(order,request.getAmount());
        Integer paymentOrder = getNextPaymentOrder(order.getId());
        Payment payment = paymentMapper.bankTransferRequestToPayment(request);
        payment.setOrder(order);
        payment.setMethod(PaymentMethod.BANK_TRANSFER);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentOrder(paymentOrder);
        Payment savedPayment = paymentRepository.save(payment);
        subtractOrderRemainingAmount(order,request.getAmount());
        return paymentMapper.paymentToInternalResponse(savedPayment);
    }

    @Override
    public PaymentInternalResponse depositPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Payment with id: " + id + " not found!"));
        if(payment.getStatus() != PaymentStatus.PENDING){
            throw new BusinessException("Only pending payments can be deposited!");
        }
        payment.setDepositDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.DEPOSITED);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.paymentToInternalResponse(savedPayment);
    }

    @Override
    public PaymentInternalResponse rejectPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Payment with id: " + id + " not found!"));
        if(payment.getStatus() != PaymentStatus.PENDING){
            throw new BusinessException("Only pending payments can be rejected!");
        }
        payment.setStatus(PaymentStatus.REJECTED);
        Payment savedPayment = paymentRepository.save(payment);
        restoreOrderRemainingAmount(payment.getOrder(),payment.getAmount());
        return paymentMapper.paymentToInternalResponse(savedPayment);
    }

    private Integer getNextPaymentOrder(Long orderId){
        Optional<Integer> maxOrder = paymentRepository.findMaxPaymentOrderByOrderId(orderId);
        return maxOrder.map(order -> order + 1).orElse(1);
    }
    private void validateOrder(Order order,BigDecimal amount){
        if(order.getStatus() != OrderStatus.PENDING){
            throw new BusinessException("Only pending orders can be paid");
        }
        if(order.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Order is fully paid");
        }
        if(amount.compareTo(order.getRemainingAmount()) > 0){
            throw new BusinessException("Payment amount exceeds remaining amount: " + order.getRemainingAmount());
        }
    }
    private void validateCashAmount(BigDecimal amount){
        if(amount.compareTo(MAX_CASH_AMOUNT) > 0){
            throw new BusinessException("Cash payment cannot exceed 20,000 DH !");
        }
    }
    private void subtractOrderRemainingAmount(Order order, BigDecimal amount){
        BigDecimal currentRemainingAmount = order.getRemainingAmount();
        BigDecimal newRemainingAmount = currentRemainingAmount.subtract(amount);
        order.setRemainingAmount(newRemainingAmount);
        orderRepository.save(order);
    }
    private void restoreOrderRemainingAmount(Order order,BigDecimal amount){
        BigDecimal currentRemainingAmount = order.getRemainingAmount();
        BigDecimal newRemainingAmount = currentRemainingAmount.add(amount);
        order.setRemainingAmount(newRemainingAmount);
        orderRepository.save(order);
    }

}
