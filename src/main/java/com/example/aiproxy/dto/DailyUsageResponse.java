package com.example.aiproxy.dto;
import java.time.LocalDate;
public record DailyUsageResponse(LocalDate date, long tokensConsumed) {}
