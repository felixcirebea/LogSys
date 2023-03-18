package ro.siit.logsys.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.siit.logsys.converter.OrderConverter;
import ro.siit.logsys.dto.OrderDto;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.enums.OrderStatus;
import ro.siit.logsys.exception.DataNotFound;
import ro.siit.logsys.helper.CompanyInfoContributor;
import ro.siit.logsys.repository.DestinationRepository;
import ro.siit.logsys.repository.OrderRepository;
import ro.siit.logsys.service.IOrderService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final DestinationRepository destinationRepository;
    private final OrderConverter orderConverter;
    private final CompanyInfoContributor infoContributor;


    public OrderServiceImpl(OrderRepository orderRepository, DestinationRepository destinationRepository,
                            OrderConverter orderConverter, CompanyInfoContributor infoContributor) {
        this.orderRepository = orderRepository;
        this.destinationRepository = destinationRepository;
        this.orderConverter = orderConverter;
        this.infoContributor = infoContributor;
    }

    @Override
    public Map<String, List<?>> saveOrder(List<OrderDto> orderDtos) {
        log.info(String.format("Saving %s new orders", orderDtos.size()));

        List<OrderDto> invalidDtos = new ArrayList<>();
        List<OrderDto> validDtos = new ArrayList<>();

        orderDtos.forEach(orderDto -> {
            LocalDate orderDtoDate = orderConverter.fromStringToLocalDate(orderDto.getDeliveryDate());
            if (orderDtoDate.isAfter(infoContributor.getCurrentDate()) &&
                    destinationRepository.findByName(orderDto.getDestination()).isPresent()) {
                validDtos.add(orderDto);
            } else {
                invalidDtos.add(orderDto);
            }
        });

        List<OrderEntity> orderEntities = orderConverter.fromDtosToEntities(validDtos);
        List<OrderEntity> savedEntities = new ArrayList<>();
        orderRepository.saveAll(orderEntities).forEach(savedEntities::add);
        List<OrderDto> toReturn = orderConverter.fromEntitiesToDtos(savedEntities);

        log.info(String.format("%s orders out of %s, where saved. %s orders are invalid!",
                toReturn.size(), orderDtos.size(), invalidDtos.size()));

        return Map.of(
                "invalidOrders", invalidDtos,
                "savedOrders", toReturn
        );
    }

    @Override
    @Transactional
    public Map<String, List<?>> cancelOrders(List<Long> ids) {
        log.info(String.format("Canceling %s orders", ids.size()));

        List<Long> failedIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();

        ids.forEach(id -> {
            Optional<OrderEntity> order = orderRepository.findById(id);
            if (order.isPresent() &&
                    order.get().getStatus() != OrderStatus.DELIVERED &&
                    order.get().getStatus() != OrderStatus.CANCELED) {
                orderRepository.updateStatusAndLastUpdatedById(
                        id, infoContributor.getCurrentDate(), OrderStatus.CANCELED);
                updatedIds.add(id);
            } else {
                failedIds.add(id);
            }
        });

        log.info(String.format("%s out of %s orders updated. %s orders failed!",
                updatedIds.size(), ids.size(), failedIds.size()));

        return Map.of("failedIds", failedIds, "updatedIds", updatedIds);
    }

    @Override
    public List<OrderDto> getOrderStatus(String date, String destination) throws DataNotFound {
        LocalDate deliveryDate = infoContributor.getCurrentDate();
        List<OrderEntity> orderEntities;

        if (date != null && (!date.equals(""))) {
            deliveryDate = orderConverter.fromStringToLocalDate(date);
        }

        if (destination != null && (!destination.equals(""))) {
            DestinationEntity destinationEntity = destinationRepository.findByName(destination)
                    .orElseThrow(() -> new DataNotFound(String.format("Destination %s not found in DB!", destination)));
            orderEntities = orderRepository.findAllByDestinationAndDeliveryDate(destinationEntity, deliveryDate);
        } else {
            orderEntities = orderRepository.findAllByDeliveryDate(deliveryDate);
        }

        return orderConverter.fromEntitiesToDtos(orderEntities);
    }
}
