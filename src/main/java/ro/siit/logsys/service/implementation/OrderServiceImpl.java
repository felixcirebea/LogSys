package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.siit.logsys.converter.OrderConverter;
import ro.siit.logsys.dto.OrderDto;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.helper.CompanyInfoContributor;
import ro.siit.logsys.repository.DestinationRepository;
import ro.siit.logsys.repository.OrderRepository;
import ro.siit.logsys.service.IOrderService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final DestinationRepository destinationRepository;
    private final OrderConverter orderConverter;
    private final CompanyInfoContributor companyInfoContributor;

    public OrderServiceImpl(OrderRepository orderRepository, DestinationRepository destinationRepository,
                            OrderConverter orderConverter, CompanyInfoContributor companyInfoContributor) {
        this.orderRepository = orderRepository;
        this.destinationRepository = destinationRepository;
        this.orderConverter = orderConverter;
        this.companyInfoContributor = companyInfoContributor;
    }

    @Override
    public Map<String, List<?>> saveOrder(List<OrderDto> orderDtos) {

        log.info(String.format("Saving %s new orders", orderDtos.size()));

        Map<String, List<?>> resultMap = new HashMap<>();
        List<OrderDto> invalidDtos = new ArrayList<>();
        List<OrderDto> validDtos = new ArrayList<>();

        orderDtos.forEach(orderDto -> {
            LocalDate orderDtoDate = orderConverter.fromStringToLocalDate(orderDto.getDeliveryDate());

            if (orderDtoDate.isAfter(companyInfoContributor.getCurrentDate()) &&
                    destinationRepository.findByName(orderDto.getDestination()).isPresent()) {
                validDtos.add(orderDto);
            } else {
                invalidDtos.add(orderDto);
            }
        });

        List<OrderEntity> orderEntities = orderConverter.fromDtoToEntity(validDtos);
        Iterable<OrderEntity> savedObjects = orderRepository.saveAll(orderEntities);
        List<OrderEntity> toConvert = StreamSupport.stream(savedObjects.spliterator(), false).toList();
        List<OrderDto> toReturn = orderConverter.fromEntityToDto(toConvert);

        log.info(String.format("%s orders out of %s, where saved. %s orders are invalid!",
                toReturn.size(), orderDtos.size(), invalidDtos.size()));

        resultMap.put("Invalid orders", invalidDtos);
        resultMap.put("Saved orders", toReturn);

        return resultMap;
    }
}
