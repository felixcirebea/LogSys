package ro.siit.logsys.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import ro.siit.logsys.enums.OrderStatus;

import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {

    private Long id;
    @NotEmpty(message = "Destination cannot be empty!")
    private String destination;
    @NotEmpty(message = "Delivery date cannot be empty!")
    private String deliveryDate;
    private OrderStatus status;
    private LocalDate lastUpdated;
}

