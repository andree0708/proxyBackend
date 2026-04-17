package com.proxy.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class GenerationRequest {
    @NotBlank(message = "Prompt is required")
    private String prompt;
    private Integer maxTokens;

    public GenerationRequest() {}

    public GenerationRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}