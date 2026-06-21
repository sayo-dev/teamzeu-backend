package com.teamzeu.velo.exceptions;

import com.teamzeu.velo.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> failure(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.failure(message));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        return failure(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<Object> handleCustomBadRequestException(CustomBadRequestException ex) {
        return failure(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<Object> handleCustomNotFoundException(CustomNotFoundException ex) {
        return failure(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<Object> handleAuthException(Exception ex) {
        return failure(HttpStatus.BAD_REQUEST, "Bad credentials");
    }

    @ExceptionHandler(CustomConflictException.class)
    public ResponseEntity<Object> handleCustomConflictException(CustomConflictException ex) {
        return failure(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return failure(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return failure(HttpStatus.FORBIDDEN, "Access Denied: You do not have permission to perform this action.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors()
                .getFirst().getDefaultMessage();

        return ResponseEntity.status(status).body(ApiResponse.failure(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleSecurityOrGeneral(Exception ex) {
        log.info(ex.getMessage());
        log.error(String.valueOf(ex.getCause()));
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred on the server.");
    }
}
