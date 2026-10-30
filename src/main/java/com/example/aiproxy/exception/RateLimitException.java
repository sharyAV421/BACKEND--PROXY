package com.example.aiproxy.exception;
public class RateLimitException extends RuntimeException {
    private final long retryAfter;
    public RateLimitException(long retryAfter) { super("Request limit exceeded. Try again later."); this.retryAfter=retryAfter; }
    public long getRetryAfter() { return retryAfter; }
}
