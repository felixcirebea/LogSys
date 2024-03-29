package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.enums.OrderStatus;
import ro.siit.logsys.exception.InputFileException;
import ro.siit.logsys.helper.CompanyInfoContributor;
import ro.siit.logsys.repository.DestinationRepository;
import ro.siit.logsys.repository.OrderRepository;
import ro.siit.logsys.service.ICsvParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CsvParserImpl implements ICsvParser {

    private final DestinationRepository destinationRepository;
    private final OrderRepository orderRepository;
    private final CompanyInfoContributor infoContributor;

    @Value("classpath:/input-files/destinations.csv")
    private Resource destinationsResource;
    @Value("classpath:/input-files/orders.csv")
    private Resource ordersResource;

    private final List<DestinationEntity> destinations = new ArrayList<>();
    private final List<OrderEntity> orders = new ArrayList<>();

    public CsvParserImpl(DestinationRepository destinationRepository, OrderRepository orderRepository,
                         CompanyInfoContributor infoContributor) {
        this.destinationRepository = destinationRepository;
        this.orderRepository = orderRepository;
        this.infoContributor = infoContributor;
    }

    @Override
    public void run() throws InputFileException {
        populateDbTable(getPath(destinationsResource), DestinationEntity.class);
        populateDbTable(getPath(ordersResource), OrderEntity.class);

        log.info("Database tables successfully populated!");
    }

    private <T> void populateDbTable(String filePath, Class<T> clazz)
            throws InputFileException {

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
        } catch (IOException e) {
            throw new InputFileException("Input file error!");
        }
    }

    private void generateOrdersCollection(String line) throws InputFileException {
        String[] split = line.split(",");
        OrderEntity entity = new OrderEntity();

        DestinationEntity destination = destinationRepository.findByName(split[0])
                .orElseThrow(() -> new InputFileException(String.format("Destination called %s not found in database", split[0])));

        entity.setDestination(destination);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        entity.setDeliveryDate(LocalDate.parse(split[1], formatter));
        entity.setStatus(OrderStatus.NEW);
        entity.setLastUpdated(infoContributor.getCurrentDate());

        orders.add(entity);
    }

    private void generateDestinationsCollection(String line) throws InputFileException {
        String[] split = line.split(",");
        DestinationEntity entity = new DestinationEntity();
        entity.setName(split[0]);

        try {
            entity.setDistance(Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            throw new InputFileException("Input file format error!");
        }
        destinations.add(entity);
    }

    private String getPath(Resource resource) throws InputFileException {
        try {
            return Paths.get(resource.getURI()).toString();
        } catch (IOException e) {
            throw new InputFileException("Input file path error!");
        }
    }

}
