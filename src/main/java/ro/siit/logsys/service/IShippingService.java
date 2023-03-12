package ro.siit.logsys.service;

import ro.siit.logsys.entity.DestinationEntity;
import ro.siit.logsys.exception.RunningThreadException;

import java.util.List;

public interface IShippingService {
    void advanceDate() throws RunningThreadException;
    void startDeliveries(DestinationEntity destination, List<Long> orderIds);
}
