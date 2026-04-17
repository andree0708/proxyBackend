package com.proxy.backend.dto;

public class GenerationResponse {
    private String text;
    private int tokensUsed;
    private long timestamp;

    public GenerationResponse() {}

    public GenerationResponse(String text, int tokensUsed) {
        this.text = text;
        this.tokensUsed = tokensUsed;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}