package com.example.tpcm_spring.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
