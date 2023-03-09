package ro.siit.logsys.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ro.siit.logsys.dto.OrderDto;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.enums.OrderStatus;
import ro.siit.logsys.repository.DestinationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrderConverter {

    private final DestinationRepository destinationRepository;

    public OrderConverter(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<OrderEntity> fromDtoToEntity(List<OrderDto> orderDtos) {
        List<OrderEntity> toReturn = new ArrayList<>();

        orderDtos.forEach(orderDto -> {
            OrderEntity entity = new OrderEntity();
            entity.setStatus(OrderStatus.NEW);
            entity.setLastUpdated(OrderStatus.NEW);
            entity.setDestination(destinationRepository.findByName(orderDto.getDestination()).get());
            entity.setDeliveryDate(fromStringToLocalDate(orderDto.getDeliveryDate()));

            toReturn.add(entity);
        });

        return toReturn;
    }

    public List<OrderDto> fromEntityToDto(List<OrderEntity> orderEntities) {
        List<OrderDto> toReturn = new ArrayList<>();

        orderEntities.forEach(orderEntity -> {
            OrderDto dto = OrderDto.builder()
                    .id(orderEntity.getId())
                    .destination(orderEntity.getDestination().getName())
                    .deliveryDate(String.valueOf(orderEntity.getDeliveryDate()))
                    .status(orderEntity.getStatus())
                    .lastUpdated(orderEntity.getLastUpdated())
                    .build();
            toReturn.add(dto);
        });

        return toReturn;
    }

    public LocalDate fromStringToLocalDate(String line) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(line, formatter);
    }

}
