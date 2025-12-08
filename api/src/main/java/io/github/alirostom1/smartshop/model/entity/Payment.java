package io.github.alirostom1.smartshop.model.entity;

import io.github.alirostom1.smartshop.enums.PaymentMethod;
import io.github.alirostom1.smartshop.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "payments")
public class Payment extends Auditable{

    @Column(name = "numero_paiement", nullable = false)
    private Integer paymentOrder;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String bank;

    @Column(nullable = false,unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "deposit_date")
    private LocalDateTime depositDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}