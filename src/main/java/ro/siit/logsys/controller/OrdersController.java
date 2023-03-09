package ro.siit.logsys.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.siit.logsys.dto.OrderDto;
import ro.siit.logsys.service.IOrderService;

import java.util.List;

@RestController
@RequestMapping("/order")
@Validated
public class OrdersController {

    private final IOrderService orderService;

    public OrdersController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addOrder(@RequestBody @Valid List<OrderDto> orderDtos) { //TODO treat exceptions
        return ResponseEntity.ok(orderService.saveOrder(orderDtos));
    }
}
