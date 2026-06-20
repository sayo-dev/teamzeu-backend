package com.teamzeu.velo.common.exceptions;

import jakarta.validation.constraints.NotBlank;

public class UserAlreadyVerified extends RuntimeException {
    public UserAlreadyVerified( String message ) {
        super(message);
    }
}
