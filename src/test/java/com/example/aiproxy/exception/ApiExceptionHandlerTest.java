package com.example.aiproxy.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler=new ApiExceptionHandler();

    @Test
    void rateLimitResponseHasJsonErrorAndRetryAfter() {
        var response=handler.rateLimit(new RateLimitException(17));
        assertEquals(429, response.getStatusCode().value());
        assertEquals("17", response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER));
        assertEquals("rate_limit_exceeded", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void quotaResponseHasConsistentJsonErrorShape() {
        var response=handler.quota(new QuotaExceededException());
        assertEquals(402, response.getStatusCode().value());
        assertEquals("quota_exceeded", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("message"));
    }
}
