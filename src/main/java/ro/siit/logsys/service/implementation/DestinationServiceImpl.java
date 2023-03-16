package ro.siit.logsys.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.siit.logsys.converter.DestinationConverter;
import ro.siit.logsys.dto.DestinationDto;
import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.exception.ArgumentNotValidException;
import ro.siit.logsys.exception.DataNotFound;
import ro.siit.logsys.repository.DestinationRepository;
import ro.siit.logsys.service.IDestinationsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DestinationServiceImpl implements IDestinationsService {

    private final DestinationRepository destinationRepository;
    private final DestinationConverter destinationConverter;

    public DestinationServiceImpl(DestinationRepository destinationRepository,
                                  DestinationConverter destinationConverter) {
        this.destinationRepository = destinationRepository;
        this.destinationConverter = destinationConverter;
    }

    @Override
    public Long addDestination(DestinationDto destinationDto) throws ArgumentNotValidException {
        log.info("Saving new destination!");

        DestinationEntity destinationEntity = destinationConverter.fromDtoToEntity(destinationDto);

        if (destinationRepository.findByName(destinationEntity.getName()).isPresent()) {
            log.error("Destination already in DB!");
            throw new ArgumentNotValidException("Destination already in DB!");
        }

        return destinationRepository.save(destinationEntity).getId();
    }

    @Override
    public Long updateDestination(DestinationDto destinationDto) throws DataNotFound {
        log.info("Updating destination with id: " + destinationDto.getId());

        Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(destinationDto.getId());
        if (destinationEntityOptional.isEmpty()) {
            log.error(String.format("Destination with id - %s - not found in DB!", destinationDto.getId()));
            throw new DataNotFound(String.format("Destination with id - %s - not found in DB!", destinationDto.getId()));
        }

        DestinationEntity destinationEntity = destinationEntityOptional.get();
        if (destinationDto.getName() != null) {
            destinationEntity.setName(destinationDto.getName());
        }
        if (destinationDto.getDistance() != null) {
            destinationEntity.setDistance(destinationDto.getDistance());
        }

        return destinationRepository.save(destinationEntity).getId();
    }

    @Override
    public List<DestinationDto> getAll() {
        log.info("Getting all destinations");

        List<DestinationDto> dtos = new ArrayList<>();

        destinationRepository.findAll().forEach(destinationEntity ->
                dtos.add(destinationConverter.fromEntityToDto(destinationEntity)));

        return dtos;
    }

    @Override
    public DestinationDto getDestinationById(String destinationId) throws DataNotFound {
        log.info("Getting destination with id: " + destinationId);

        Optional<DestinationEntity> entity = destinationRepository.findById(Long.valueOf(destinationId));
        if (entity.isEmpty()) {
            throw new DataNotFound(String.format("Entry with id - %s - not found in DB!", destinationId));
        }

        return destinationConverter.fromEntityToDto(entity.get());
    }
}
