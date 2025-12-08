package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.enums.OrderStatus;
import io.github.alirostom1.smartshop.model.entity.Client;
import io.github.alirostom1.smartshop.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{
    //INTERNAL
    Page<Order> findByClient_Id(Long clientId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status,Pageable pageable);

    //PUBLIC
    Page<Order> findByClient_User_Id(Long userId,Pageable pageable);
    Optional<Order> findByPublicId(UUID publicId);

    //STATS
    Long countOrdersByClientAndStatus(Client client, OrderStatus status);
    Long countOrdersByClient(Client client);


    @Query("SELECT COALESCE(SUM(o.totalTTC),0) FROM Order o WHERE o.client = :client AND o.status = :status ")
    BigDecimal sumTotalTTCByClientAndStatus(@Param("client") Client client,
                                            @Param("status") OrderStatus status);

    @Query("SELECT MIN(o.createdAt) FROM Order o WHERE o.client = :client")
    LocalDateTime getFirstOrderDateTimeByClient(@Param("client") Client client);

    @Query("SELECT MAX(o.createdAt) FROM Order o WHERE o.client = :client")
    LocalDateTime getLastOrderDateTimeByClient(@Param("client") Client client);
}
