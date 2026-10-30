package com.example.aiproxy.service;

import com.example.aiproxy.dto.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MockAIGenerationService implements AIGenerationService {
    private static final List<String> RESPONSES = List.of("La inteligencia artificial puede ayudarte a resolver este problema paso a paso.", "Una buena solución comienza por comprender los datos y el objetivo.", "Aquí tienes una respuesta generada para tu solicitud.", "Podemos abordar esta tarea con un enfoque sencillo y eficiente.");
    @Override public GenerationResponse generate(GenerationRequest request) {
        try { Thread.sleep(1200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException("Generation was interrupted."); }
        return new GenerationResponse(RESPONSES.get(ThreadLocalRandom.current().nextInt(RESPONSES.size())), 0);
    }
}
