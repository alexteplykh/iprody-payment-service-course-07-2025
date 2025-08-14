package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.paymentserviceapp.exception.ErrorResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EntityNotFoundException ex) {
        return new ErrorResponse(
                ex.getId(),
                ex.getOperation(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotFound(TypeMismatchException ex) {
        return new ErrorResponse(
                null,
                null,
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOther(Exception ex) {
        return new ErrorResponse(
                null,
                null,
                ex.getMessage()
        );
    }
}
