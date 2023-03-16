package ro.siit.logsys.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.siit.logsys.exception.RunningThreadException;
import ro.siit.logsys.helper.CompanyInfoContributor;
import ro.siit.logsys.service.IShippingService;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final IShippingService shippingService;
    private final CompanyInfoContributor infoContributor;

    public ShippingController(IShippingService shippingService, CompanyInfoContributor infoContributor) {
        this.shippingService = shippingService;
        this.infoContributor = infoContributor;
    }

    @PostMapping("/new-day")
    public ResponseEntity<String> advanceDate() throws RunningThreadException {
        shippingService.advanceDate();
        return ResponseEntity.ok("New day starting: " + infoContributor.getCurrentDate());
    }
}
