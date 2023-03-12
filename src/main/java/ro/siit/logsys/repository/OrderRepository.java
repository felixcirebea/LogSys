package ro.siit.logsys.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.enums.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, Long> {

    @Modifying
    @Query(value = "UPDATE orders o SET o.status = :status, o.lastUpdated = :currentDate WHERE o.id = :orderId")
    void updateStatusAndLastUpdatedById(@Param("orderId") Long orderId,
                                        @Param("currentDate") LocalDate currentDate,
                                        @Param("status") OrderStatus status);

    @Query(value = "SELECT o FROM orders o WHERE o.deliveryDate = :date")
    List<OrderEntity> findAllByDeliveryDate(@Param("date") LocalDate date);

    @Query(value = "SELECT o FROM orders o WHERE o.destination = :destination AND o.deliveryDate = :deliveryDate")
    List<OrderEntity> findAllByDestinationAndDeliveryDate(@Param("destination") DestinationEntity destination,
                                                          @Param("deliveryDate") LocalDate deliveryDate);
}
