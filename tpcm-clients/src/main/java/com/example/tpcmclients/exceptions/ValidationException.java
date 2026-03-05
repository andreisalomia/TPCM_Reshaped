package com.example.tpcm-clients.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
