package com.example.tpcm-clients.exceptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorMessageTemplate {
    private int status;
    private String error;
    private String message;
}
