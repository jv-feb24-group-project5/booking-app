package com.ua.accommodation.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@ControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(
            AccessDeniedException ex, WebRequest request
    ) {
        String bodyOfResponse = "Access denied: " + ex.getMessage();
        log.error("AccessDeniedException occured", ex);
        return getResponseEntity(HttpStatus.FORBIDDEN, bodyOfResponse);
    }

    private ResponseEntity<Object> getResponseEntity(HttpStatus status, Object errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.getReasonPhrase());
        body.put("errors", errors);
        return new ResponseEntity<>(body, status);
    }
}
