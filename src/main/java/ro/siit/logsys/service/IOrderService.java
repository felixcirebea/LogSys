package ro.siit.logsys.service;

import ro.siit.logsys.dto.OrderDto;
import ro.siit.logsys.exception.DataNotFound;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    Map<String, List<?>> saveOrder(List<OrderDto> orderDtos);
    Map<String, List<?>> cancelOrders(List<Long> ids);
    List<OrderDto> getOrderStatus(String deliveryDate, String destination)
            throws DataNotFound;
}
