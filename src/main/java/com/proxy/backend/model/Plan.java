package com.proxy.backend.model;

public enum Plan {
    FREE(10, 50_000),
    PRO(60, 500_000),
    ENTERPRISE(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int requestsPerMinute;
    private final int monthlyTokens;

    Plan(int requestsPerMinute, int monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public int getMonthlyTokens() {
        return monthlyTokens;
    }
}