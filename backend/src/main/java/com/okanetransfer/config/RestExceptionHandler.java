package com.okanetransfer.config;

import com.okanetransfer.exception.AgencyNotFoundException;
import com.okanetransfer.exception.InvalidDailyLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(AgencyNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleAgencyNotFound(AgencyNotFoundException ex) {
        log.warn("Agency not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidDailyLimitException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleInvalidDailyLimit(InvalidDailyLimitException ex) {
        log.warn("Invalid daily limit", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Unhandled runtime error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Internal server error"));
    }
}
