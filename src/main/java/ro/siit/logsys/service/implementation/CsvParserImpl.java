package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.enums.OrderStatus;
import ro.siit.logsys.repository.DestinationRepository;
import ro.siit.logsys.repository.OrderRepository;
import ro.siit.logsys.service.ICsvParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CsvParserImpl implements ICsvParser {

    private final DestinationRepository destinationRepository;
    private final OrderRepository orderRepository;

    @Value("classpath:/input-files/destinations.csv")
    private Resource destinationsResource;
    @Value("classpath:/input-files/orders.csv")
    private Resource ordersResource;

    private final List<DestinationEntity> destinations = new ArrayList<>();
    private final List<OrderEntity> orders = new ArrayList<>();

    public CsvParserImpl(DestinationRepository destinationRepository, OrderRepository orderRepository) {
        this.destinationRepository = destinationRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run() {
        populateDbTable(getPath(destinationsResource), DestinationEntity.class);
        populateDbTable(getPath(ordersResource), OrderEntity.class);

        log.info("Database tables successfully populated!");
    }

    private <T> void populateDbTable(String filePath, Class<T> clazz) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (clazz == DestinationEntity.class) {
                    generateDestinationsCollection(line);
                    destinationRepository.saveAll(destinations);
                } else if (clazz == OrderEntity.class) {
                    generateOrdersCollection(line);
                    orderRepository.saveAll(orders);
                }
            }
        } catch (FileNotFoundException e) { //TODO treat exceptions
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateOrdersCollection(String line) {

        String[] split = line.split(",");
        OrderEntity entity = new OrderEntity();

        Optional<DestinationEntity> destination = destinationRepository.findByName(split[0]);
        if (destination.isEmpty()) {
            throw new RuntimeException(String.format("Destination called %s not found in database", split[0]));
        }

        entity.setDestination(destination.get());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        entity.setDeliveryDate(LocalDate.parse(split[1], formatter));

        entity.setStatus(OrderStatus.NEW);
        entity.setLastUpdated(OrderStatus.NEW);

        orders.add(entity);
    }

    private void generateDestinationsCollection(String line) {

        String[] split = line.split(",");
        DestinationEntity entity = new DestinationEntity();
        entity.setName(split[0]);

        try {
            entity.setDistance(Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        destinations.add(entity);
    }

    private String getPath(Resource resource) {
        try {
            return Paths.get(resource.getURI()).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
