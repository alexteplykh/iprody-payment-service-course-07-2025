package com.iprody.paymentserviceapp.exception;

import java.time.Instant;
import java.util.UUID;

public record ErrorResponse(UUID id, String operation, String errorMessage, Instant timestamp) {
    public ErrorResponse(UUID id, String operation, String errorMessage) {
        this(id, operation, errorMessage, Instant.now());
    }
}
