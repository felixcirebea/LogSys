package ro.siit.logsys.service;

import ro.siit.logsys.dto.DestinationDto;
import ro.siit.logsys.exception.ArgumentNotValidException;
import ro.siit.logsys.exception.DataNotFound;

import java.util.List;

public interface IDestinationsService {

    Long addDestination(DestinationDto destinationDtos) throws ArgumentNotValidException;
    Long updateDestination(DestinationDto destinationDto) throws DataNotFound, ArgumentNotValidException;
    List<DestinationDto> getAllDestinations();
    DestinationDto getDestinationById(String destinationId) throws DataNotFound;
    void deleteById(Long destinationId) throws DataNotFound;
}
