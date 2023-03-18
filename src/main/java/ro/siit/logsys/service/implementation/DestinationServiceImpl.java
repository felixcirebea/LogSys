package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.siit.logsys.converter.DestinationConverter;
import ro.siit.logsys.dto.DestinationDto;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.entity.OrderEntity;
import ro.siit.logsys.exception.ArgumentNotValidException;
import ro.siit.logsys.exception.DataNotFound;
import ro.siit.logsys.repository.DestinationRepository;
import ro.siit.logsys.repository.OrderRepository;
import ro.siit.logsys.service.IDestinationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DestinationServiceImpl implements IDestinationsService {

    private final DestinationRepository destinationRepository;
    private final OrderRepository orderRepository;
    private final DestinationConverter destinationConverter;

    public DestinationServiceImpl(DestinationRepository destinationRepository,
                                  OrderRepository orderRepository, DestinationConverter destinationConverter) {
        this.destinationRepository = destinationRepository;
        this.orderRepository = orderRepository;
        this.destinationConverter = destinationConverter;
    }

    @Override
    public Long addDestination(DestinationDto destinationDto) throws ArgumentNotValidException {
        log.info("Saving new destination!");

        DestinationEntity destinationEntity = destinationConverter.fromDtoToEntity(destinationDto);
        if (destinationRepository.findByName(destinationEntity.getName()).isPresent()) {
            throw new ArgumentNotValidException("Entry already present!");
        }
        return destinationRepository.save(destinationEntity).getId();
    }

    @Override
    public Long updateDestination(DestinationDto destinationDto) throws DataNotFound, ArgumentNotValidException {
        log.info("Updating destination with id: " + destinationDto.getId());

        DestinationEntity destinationEntity = destinationRepository.findById(destinationDto.getId())
                .orElseThrow(() -> new DataNotFound(
                        String.format("Destination with id - %s - not found in DB!", destinationDto.getId())));

        Optional<DestinationEntity> entityByName = destinationRepository.findByName(destinationDto.getName());
        if (entityByName.isPresent() && (!entityByName.get().getId().equals(destinationDto.getId()))) {
            throw new ArgumentNotValidException("Destination already present with another id!");
        }

        if (destinationDto.getName() != null && (!destinationDto.getName().equals(""))) {
            destinationEntity.setName(destinationDto.getName());
        }
        if (destinationDto.getDistance() != null) {
            destinationEntity.setDistance(destinationDto.getDistance());
        }

        return destinationRepository.save(destinationEntity).getId();
    }

    @Override
    public List<DestinationDto> getAllDestinations() {
        log.info("Getting all destinations");

        List<DestinationDto> dtos = new ArrayList<>();
        destinationRepository.findAll().forEach(destinationEntity ->
                dtos.add(destinationConverter.fromEntityToDto(destinationEntity)));

        return dtos;
    }

    @Override
    public DestinationDto getDestinationById(String destinationId) throws DataNotFound {
        log.info("Getting destination with id: " + destinationId);

        DestinationEntity entity = destinationRepository.findById(Long.valueOf(destinationId))
                .orElseThrow(() -> new DataNotFound(String.format("Entry with id - %s - not found in DB!", destinationId)));
        return destinationConverter.fromEntityToDto(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long destinationId) throws DataNotFound {
        DestinationEntity destinationEntity = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new DataNotFound(String.format(
                        "Entry with id - %s - not found in DB!", destinationId)));

        List<OrderEntity> orders = orderRepository.findAllByDestination(destinationEntity);
        if (orders.isEmpty()) {
            destinationRepository.deleteById(destinationEntity.getId());
        } else {
            orders.forEach(order -> order.setDestination(null));
            orderRepository.saveAll(orders);
            destinationRepository.deleteById(destinationEntity.getId());
        }

    }

}
