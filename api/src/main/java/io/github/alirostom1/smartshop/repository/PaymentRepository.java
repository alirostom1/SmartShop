package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long>{

    @Query("SELECT MAX(p.paymentOrder) FROM Payment p WHERE p.order.id = :orderId")
    Optional<Integer> findMaxPaymentOrderByOrderId(@Param("orderId") Long orderId);

    boolean existsByReference(String reference);
}
