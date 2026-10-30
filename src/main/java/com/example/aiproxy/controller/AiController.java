package com.example.aiproxy.controller;

import com.example.aiproxy.dto.*;
import com.example.aiproxy.proxy.RateLimitProxy;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final RateLimitProxy generation;
    public AiController(RateLimitProxy generation) { this.generation=generation; }
    @PostMapping("/generate")
    public GenerationResponse generate(@RequestHeader("X-User-Id") String userId, @Valid @RequestBody GenerationRequest body) {
        return generation.generate(new GenerationRequest(body.prompt(), userId));
    }
}
