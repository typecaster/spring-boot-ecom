package com.ecommerce.project.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class MyGlobalExceptionHandler {

    /**
     * Handles all uncaught exceptions. Returns a generic error message to the user with a 500 status code.
     *
     * @param e the exception to be handled
     * @return a ResponseEntity containing the error message and the HttpStatus.INTERNAL_SERVER_ERROR status code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions of type ConstraintViolationException. Extracts violation messages
     * and returns them in a list format with a BAD_REQUEST status.
     *
     * @param e the ConstraintViolationException to be handled
     * @return a ResponseEntity containing a list of error messages and the HttpStatus.BAD_REQUEST status code
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errorMessages.add(fieldName + ": " + errorMessage);
        }
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles exceptions of type MyResourceNotFoundException. Returns the message of the exception with a 404 status code.
     *
     * @param e the MyResourceNotFoundException to be handled
     * @return a ResponseEntity containing the message of the exception and the HttpStatus.NOT_FOUND status code
     */
    @ExceptionHandler(MyResourceNotFoundException.class)
    public ResponseEntity<String> myResourceNotFoundException(MyResourceNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
