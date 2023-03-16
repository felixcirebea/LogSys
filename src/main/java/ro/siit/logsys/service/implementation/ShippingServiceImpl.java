package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.enums.OrderStatus;
import ro.siit.logsys.exception.RunningThreadException;
import ro.siit.logsys.helper.CompanyInfoContributor;
import ro.siit.logsys.repository.OrderRepository;
import ro.siit.logsys.service.IShippingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShippingServiceImpl implements IShippingService {
    private final AsyncService asyncService;

    private final CompanyInfoContributor infoContributor;
    private final OrderRepository orderRepository;
    @Qualifier("deliveryThread")
    private final ThreadPoolExecutor executor;

    public ShippingServiceImpl(AsyncService asyncService, CompanyInfoContributor infoContributor,
                               OrderRepository orderRepository,
                               ThreadPoolExecutor executor) {
        this.asyncService = asyncService;
        this.infoContributor = infoContributor;
        this.orderRepository = orderRepository;
        this.executor = executor;
    }

    @Override
    public void advanceDate() throws RunningThreadException {

        if (executor.getActiveCount() != 0) {
            throw new RunningThreadException(String.format(
                    "Deliveries for %s not finished", infoContributor.getCurrentDate())
            );
        }

        infoContributor.incrementDate();

        LocalDate currentDate = infoContributor.getCurrentDate();
        log.info("New day starting: " + currentDate);

        List<OrderEntity> ordersByDeliveryDate = orderRepository.findAllByDeliveryDate(currentDate);

        Map<DestinationEntity, List<OrderEntity>> ordersGroupedByDestination = ordersByDeliveryDate.stream()
                .collect(Collectors.groupingBy(OrderEntity::getDestination));

        String message = "Today will be delivering to " +
                ordersGroupedByDestination.keySet().stream()
                        .map(DestinationEntity::getName)
                        .collect(Collectors.joining(", "));
        log.info(message);

        ordersGroupedByDestination.forEach((destination, orderEntities) -> {
            orderEntities.stream()
                    .filter(entity -> entity.getStatus() != OrderStatus.CANCELED)
                    .forEach(entity -> {
                        entity.setStatus(OrderStatus.DELIVERING);
                        entity.setLastUpdated(currentDate);
                        orderRepository.save(entity);
                    });

            List<Long> orderIds = orderEntities.stream()
                    .map(OrderEntity::getId)
                    .toList();
            asyncService.startDeliveries(destination, orderIds);
        });
    }

}
