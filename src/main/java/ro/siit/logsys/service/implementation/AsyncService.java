package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.enums.OrderStatus;
import ro.siit.logsys.helper.CompanyInfoContributor;
import ro.siit.logsys.repository.OrderRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AsyncService {
    private final OrderRepository orderRepository;
    private final CompanyInfoContributor infoContributor;

    public AsyncService(OrderRepository orderRepository, CompanyInfoContributor infoContributor) {
        this.orderRepository = orderRepository;
        this.infoContributor = infoContributor;
    }

    @Async("deliveryThread")
    public void startDeliveries(DestinationEntity destination, List<Long> orderIds) {
        log.info(String.format("Starting %s deliveries for %s on %s for %s km",
                orderIds.size(), destination.getName(), Thread.currentThread().getName(), destination.getDistance()));

        try {
            Thread.sleep(destination.getDistance() * 1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        AtomicInteger canceledCount = new AtomicInteger();
        orderRepository.findAllById(orderIds).forEach(entity -> {
            if (entity.getStatus() == OrderStatus.CANCELED) {
                canceledCount.getAndIncrement();
            } else {
                entity.setStatus(OrderStatus.DELIVERED);
                entity.setLastUpdated(infoContributor.getCurrentDate());
                orderRepository.save(entity);
            }
        });
        int deliveredOrders = orderIds.size() - canceledCount.intValue();
        infoContributor.addProfit(deliveredOrders * destination.getDistance());

        log.info(String.format("%s deliveries completed for %s", deliveredOrders, destination.getName()));
    }
}
