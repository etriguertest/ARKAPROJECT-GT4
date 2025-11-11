package com.arka.order.Repository;

import com.arka.order.Entity.Order;
import com.arka.order.Utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.orderDate <= :date")
    List<Order> findPendingOrdersOlderThan(
            @Param("status") OrderStatus status,
            @Param("date") LocalDateTime date
    );

    long countByStatus(OrderStatus status);
    long countByStatusNot(OrderStatus status);
}
