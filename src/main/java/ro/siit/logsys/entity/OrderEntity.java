package ro.siit.logsys.entity;

import jakarta.persistence.*;
import lombok.Data;
import ro.siit.logsys.enums.OrderStatus;

import java.time.LocalDate;

@Entity(name = "orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private DestinationEntity destination;
    private LocalDate deliveryDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Enumerated(EnumType.STRING)
    private OrderStatus lastUpdated;

}
