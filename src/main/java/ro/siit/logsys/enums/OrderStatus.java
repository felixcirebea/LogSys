package ro.siit.logsys.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum OrderStatus {

    NEW(1),
    DELIVERING(2),
    DELIVERED(3),
    CANCELED(4);

    final int dbValue;

    OrderStatus(int dbValue) {
        this.dbValue = dbValue;
    }

    public Optional<Object> fromDbValue(int dbValue) {

        for (OrderStatus value : OrderStatus.values()) {
            if (value.getDbValue() == dbValue) {
                return Optional.of(value);
            }
        }
        return Optional.empty();

    }
}
