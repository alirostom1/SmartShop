package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.enums.OrderStatus;
import io.github.alirostom1.smartshop.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long>{
    //INTERNAL
    Page<Order> findByClient_Id(Long clientId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status,Pageable pageable);

    //PUBLIC
    Page<Order> findByClient_User_Id(Long userId,Pageable pageable);
    Optional<Order> findByPublicId(UUID publicId);
}
