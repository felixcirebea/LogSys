package ro.siit.logsys.service;

import ro.siit.logsys.exception.RunningThreadException;

public interface IShippingService {
    void advanceDate() throws RunningThreadException;
}
