package ro.siit.logsys.service;

import ro.siit.logsys.entity.DestinationEntity;

import java.util.List;

public interface IAsyncService {
    void startDeliveries(DestinationEntity destinationEntity, List<Long> orderIds);
}
