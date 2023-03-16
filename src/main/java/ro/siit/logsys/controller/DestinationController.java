package ro.siit.logsys.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.siit.logsys.dto.DestinationDto;
import ro.siit.logsys.exception.ArgumentNotValidException;
import ro.siit.logsys.exception.DataNotFound;
import ro.siit.logsys.helper.Validator;
import ro.siit.logsys.service.IDestinationsService;

import java.util.List;

@RestController
@RequestMapping("/destinations")
public class DestinationController {
    private final IDestinationsService destinationsService;
    private final Validator validator;

    public DestinationController(IDestinationsService destinationsService, Validator validator) {
        this.destinationsService = destinationsService;
        this.validator = validator;
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addOrder(@RequestBody @Valid DestinationDto destinationDto)
            throws ArgumentNotValidException {
        if (destinationDto.getName() == null || destinationDto.getDistance() == null) {
            throw new ArgumentNotValidException("Name and distance cannot be null!");
        }
        return new ResponseEntity<>(destinationsService.addDestination(destinationDto), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Long> updateDestination(@RequestBody @Valid DestinationDto destinationDto)
            throws DataNotFound, ArgumentNotValidException {
        if (destinationDto.getId() == null) {
            throw new ArgumentNotValidException("Id cannot be null!");
        }
        return new ResponseEntity<>(destinationsService.updateDestination(destinationDto), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<DestinationDto>> getAllDestinations() {
        return new ResponseEntity<>(destinationsService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinationDto> getDestination(@PathVariable(name = "id") String destinationId)
            throws ArgumentNotValidException, DataNotFound {
        validator.idValidator(destinationId);
        return new ResponseEntity<>(destinationsService.getDestinationById(destinationId), HttpStatus.OK);
    }
}
