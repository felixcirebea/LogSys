package ro.siit.logsys.helper;

import org.springframework.stereotype.Component;
import ro.siit.logsys.exception.ArgumentNotValidException;

@Component
public class Validator {

    public Long idValidator(String inputId) throws ArgumentNotValidException {
        Long id;
        try {
            id = Long.valueOf(inputId);
        } catch (NumberFormatException e) {
            throw new ArgumentNotValidException("The given id is not a number!");
        }
        return id;
    }

}
