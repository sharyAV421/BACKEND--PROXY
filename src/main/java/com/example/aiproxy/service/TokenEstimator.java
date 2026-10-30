package com.example.aiproxy.service;
import org.springframework.stereotype.Component;
@Component
public class TokenEstimator {
    public long estimate(String text) { return Math.max(1, (text.length() + 3L) / 4L); }
}
