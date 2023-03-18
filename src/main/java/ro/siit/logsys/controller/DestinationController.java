package ro.siit.logsys.controller;

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
    public ResponseEntity<Long> addOrder(@RequestBody DestinationDto destinationDto) throws ArgumentNotValidException {
        if (destinationDto.getName() == null || destinationDto.getDistance() == null) {
            throw new ArgumentNotValidException("Name and distance cannot be null!");
        }
        return ResponseEntity.ok(destinationsService.addDestination(destinationDto));
    }

    @PutMapping("/update")
    public ResponseEntity<Long> updateDestination(@RequestBody DestinationDto destinationDto)
            throws DataNotFound, ArgumentNotValidException {
        if (destinationDto.getId() == null) {
            throw new ArgumentNotValidException("Id cannot be null!");
        }
        return ResponseEntity.ok(destinationsService.updateDestination(destinationDto));
    }

    @GetMapping
    public ResponseEntity<List<DestinationDto>> getAllDestinations() {
        return ResponseEntity.ok(destinationsService.getAllDestinations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinationDto> getDestination(@PathVariable(name = "id") String destinationId)
            throws ArgumentNotValidException, DataNotFound {
        validator.idValidator(destinationId);
        return ResponseEntity.ok(destinationsService.getDestinationById(destinationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDestination(@PathVariable(name = "id") String destinationId)
            throws ArgumentNotValidException, DataNotFound {
        Long id = validator.idValidator(destinationId);
        destinationsService.deleteById(id);
        return ResponseEntity.ok("Success");
    }
}
