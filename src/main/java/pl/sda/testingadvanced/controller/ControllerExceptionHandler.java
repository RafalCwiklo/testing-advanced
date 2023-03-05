package pl.sda.testingadvanced.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.sda.testingadvanced.exceptions.NoSuchClientException;

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler({NoSuchClientException.class})
    ResponseEntity<String> handleException(final NoSuchClientException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
