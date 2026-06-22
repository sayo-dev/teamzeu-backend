package com.teamzeu.velo.common.exceptions;

import com.teamzeu.velo.common.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorHandler> handleUserNotFoundException(UserNotFoundException e) {
        ErrorHandler errorHandler = new ErrorHandler(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorHandler, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorHandler> handleInvalidOtpException(InvalidOtpException e) {
        ErrorHandler errorHandler = new ErrorHandler(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OtpAlreadyUsedException.class)
    public ResponseEntity<ErrorHandler> handleOtpAlreadyUsedException(OtpAlreadyUsedException e) {
        ErrorHandler errorHandler = new ErrorHandler(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorHandler> handleOtpExpiredException(OtpExpiredException e) {
        ErrorHandler errorHandler = new ErrorHandler(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorHandler> handleUserAlreadyExistException(UserAlreadyExistException e) {
        ErrorHandler errorHandler = new ErrorHandler(
                e.getMessage(),
                HttpStatus.CONFLICT.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorHandler, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserAlreadyVerified.class)
    public ResponseEntity<ErrorHandler> handleUserAlreadyVerified(UserAlreadyVerified e) {
        ErrorHandler errorHandler = new ErrorHandler(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorHandler> handleValidationErrors(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorHandler errorHandler = new ErrorHandler(
                errorMessage,
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );

        List<String> errorMessages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        String firstError = errorMessage.isEmpty() ? "Validation error" : errorMessages.get(0);
        ErrorHandler error = new ErrorHandler(
                firstError,
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHandler> handleAll(Exception ex) {
        ErrorHandler error = new ErrorHandler(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
