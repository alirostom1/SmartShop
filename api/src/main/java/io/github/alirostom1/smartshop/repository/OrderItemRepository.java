package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long>{

}
