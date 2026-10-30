package com.example.aiproxy.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(RateLimitException.class)
    ResponseEntity<Map<String,String>> rateLimit(RateLimitException e) { return ResponseEntity.status(429).header(HttpHeaders.RETRY_AFTER, String.valueOf(e.getRetryAfter())).body(Map.of("error", "rate_limit_exceeded", "message", e.getMessage())); }
    @ExceptionHandler(QuotaExceededException.class)
    ResponseEntity<Map<String,String>> quota(QuotaExceededException e) { return ResponseEntity.status(402).body(Map.of("error", "quota_exceeded", "message", e.getMessage())); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,String>> invalid(MethodArgumentNotValidException e) { return ResponseEntity.badRequest().body(Map.of("error", "validation_error", "message", "Prompt must not be blank.")); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", "bad_request", "message", e.getMessage())); }
}
