package com.teamzeu.velo.common.exceptions;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(String message) {
        super(message);
    }
}
