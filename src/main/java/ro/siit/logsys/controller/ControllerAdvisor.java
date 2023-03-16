package ro.siit.logsys.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ro.siit.logsys.exception.ArgumentNotValidException;
import ro.siit.logsys.exception.DataNotFound;
import ro.siit.logsys.exception.InputFileException;
import ro.siit.logsys.exception.RunningThreadException;

import java.time.DateTimeException;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationException(Exception ex, WebRequest webRequest) {
        int i = ex.getMessage().indexOf(" ");
        String errorMessage = ex.getMessage().substring(i);
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler({InputFileException.class})
    public void handleInputException(Exception ex) {
        log.error(ex.getMessage());
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<Object> handleThreadException(Exception ex, WebRequest webRequest) {
        String message = "Delivery thread interrupted!";
        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    @ExceptionHandler({DataNotFound.class, DateTimeException.class,
            RunningThreadException.class, ArgumentNotValidException.class})
    public ResponseEntity<Object> handleGeneralException (Exception ex, WebRequest webRequest) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }
}
