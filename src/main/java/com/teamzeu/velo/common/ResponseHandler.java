package com.teamzeu.velo.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResponseHandler {

    public ResponseEntity<Object> responseHandler(String message, HttpStatus statusCode, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("statusCode", statusCode.value());

        if (data != null) {
            response.put("data", data);
        }
        return new ResponseEntity<>(response, statusCode);
    }
}
