package com.eteration.simplebanking.controller;

import com.eteration.simplebanking.model.exception.AlreadyExistsException;
import com.eteration.simplebanking.model.exception.ExceptionModel;
import com.eteration.simplebanking.model.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionModel> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        ExceptionModel exception = ExceptionModel.builder()
                .timestamp(ZonedDateTime.now())
                .statusCode(400)
                .errorCode("INSUFFICIENT_BALANCE")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(exception, HttpStatus.resolve(400));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionModel> handleInsufficientBalanceException(NotFoundException ex) {
        ExceptionModel exception = ExceptionModel.builder()
                .timestamp(ZonedDateTime.now())
                .statusCode(400)
                .errorCode("NOT_FOUND")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(exception, HttpStatus.resolve(404));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionModel> handleInsufficientBalanceException(AlreadyExistsException ex) {
        ExceptionModel exception = ExceptionModel.builder()
                .timestamp(ZonedDateTime.now())
                .statusCode(409)
                .errorCode("ALREADY_EXISTS")
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<>(exception, HttpStatus.resolve(404));
    }
}
