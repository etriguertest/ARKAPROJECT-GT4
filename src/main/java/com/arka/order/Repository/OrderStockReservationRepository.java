package com.arka.order.Repository;

import com.arka.order.Entity.OrderStockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderStockReservationRepository extends JpaRepository<OrderStockReservation,Long> {
    @Query("SELECT COALESCE(SUM(r.reservedQuantity), 0) FROM OrderStockReservation r " +
            "WHERE r.productId = :productId AND r.status = 'PENDIENTE'")
    Integer getTotalReservedByProduct(@Param("productId") Long productId);

    List<OrderStockReservation> findByOrderId(Long orderId);
    List<OrderStockReservation> findByOrderIdAndProductIdIn(Long orderId, List<Long> productIds);
}
