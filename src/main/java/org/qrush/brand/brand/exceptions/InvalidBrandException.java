package org.qrush.brand.brand.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)  // Maps to HTTP 400
public class InvalidBrandException extends RuntimeException {
    public InvalidBrandException(String message) {
        super(message);
    }
}

