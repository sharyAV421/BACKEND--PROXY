package com.example.aiproxy.dto;
import jakarta.validation.constraints.NotBlank;
public record GenerationRequest(@NotBlank String prompt, String userId) {}
