package com.iprody.paymentserviceapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    private UUID id;
    private String operation;

    public EntityNotFoundException(UUID id, String op, String message) {
        super(message);
        this.id = id;
        this.operation = op;
    }


}
