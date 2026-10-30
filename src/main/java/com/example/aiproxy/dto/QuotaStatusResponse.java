package com.example.aiproxy.dto;
import com.example.aiproxy.model.Plan;
import java.time.LocalDate;
public record QuotaStatusResponse(Plan plan, long tokensUsed, long tokensAvailable, LocalDate resetDate) {}
