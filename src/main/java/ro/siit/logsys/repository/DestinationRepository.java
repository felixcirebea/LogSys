package ro.siit.logsys.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ro.siit.logsys.entity.DestinationEntity;

import java.util.Optional;

public interface DestinationRepository extends CrudRepository<DestinationEntity, Long> {
    @Query(value = "SELECT d FROM destinations d WHERE d.name = :name")
    Optional<DestinationEntity> findByName(@Param("name") String name);

}
