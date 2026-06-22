package com.teamzeu.velo.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHandler {
    private String message;
    private Integer StatusCode;
    private Long timestamp;
}
