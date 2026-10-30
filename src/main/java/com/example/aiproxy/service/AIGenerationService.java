package com.example.aiproxy.service;
import com.example.aiproxy.dto.GenerationRequest;
import com.example.aiproxy.dto.GenerationResponse;
public interface AIGenerationService { GenerationResponse generate(GenerationRequest request); }
