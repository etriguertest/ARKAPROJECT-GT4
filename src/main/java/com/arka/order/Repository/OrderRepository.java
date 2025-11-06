package com.arka.order.Repository;

import com.arka.order.Entity.Order;
import com.arka.order.Utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatus(OrderStatus status);
}
