package pl.sda.testingadvanced.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.sda.testingadvanced.exceptions.NoSuchClientException;
import pl.sda.testingadvanced.exceptions.NotEnoughResources;

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(NoSuchClientException.class)
    ResponseEntity<String> handleException(final NoSuchClientException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(NotEnoughResources.class)
    ResponseEntity<String> handleException(final NotEnoughResources e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
