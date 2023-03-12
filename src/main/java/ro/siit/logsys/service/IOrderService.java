package ro.siit.logsys.service;

import ro.siit.logsys.dto.OrderDto;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    Map<String, List<?>> saveOrder(List<OrderDto> orderDtos);
}
