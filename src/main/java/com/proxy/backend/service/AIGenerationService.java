package com.proxy.backend.service;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;

public interface AIGenerationService {
    GenerationResponse generate(GenerationRequest request);
}