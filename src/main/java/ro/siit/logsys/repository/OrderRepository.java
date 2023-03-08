package ro.siit.logsys.repository;

import org.springframework.data.repository.CrudRepository;
import ro.siit.logsys.entity.OrderEntity;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
}
