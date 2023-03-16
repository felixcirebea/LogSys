package ro.siit.logsys.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.siit.logsys.dto.OrderDto;
import ro.siit.logsys.exception.DataNotFound;
import ro.siit.logsys.service.IOrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrdersController {

    private final IOrderService orderService;

    public OrdersController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addOrder(@RequestBody @Valid List<OrderDto> orderDtos) {
        return ResponseEntity.ok(orderService.saveOrder(orderDtos));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Object> cancelOrder(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(orderService.cancelOrders(ids));
    }

    @GetMapping("/status")
    public ResponseEntity<List<OrderDto>> getStatus(@RequestParam(name = "date", required = false) String date,
                                            @RequestParam(name = "destination", required = false) String destination)
            throws DataNotFound {
        return ResponseEntity.ok(orderService.getOrderStatus(date, destination));
    }
}
