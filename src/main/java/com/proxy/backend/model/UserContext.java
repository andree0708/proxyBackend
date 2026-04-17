package com.proxy.backend.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserContext {

    private final ConcurrentHashMap<String, UserState> users = new ConcurrentHashMap<>();

    public UserState getOrCreateUser(String userId) {
        return users.computeIfAbsent(userId, k -> new UserState());
    }

    public static class UserState {
        private Plan plan = Plan.FREE;
        private AtomicInteger monthlyTokensUsed = new AtomicInteger(0);
        private AtomicInteger requestsThisMinute = new AtomicInteger(0);
        private LocalDateTime lastRequestTime = null;
        private LocalDateTime monthStartDate = LocalDateTime.now();
        private List<DailyUsage> usageHistory = new ArrayList<>();

        public Plan getPlan() {
            return plan;
        }

        public void setPlan(Plan plan) {
            this.plan = plan;
        }

        public int getMonthlyTokensUsed() {
            return monthlyTokensUsed.get();
        }

        public void addTokens(int tokens) {
            monthlyTokensUsed.addAndGet(tokens);
        }

        public void resetMonthlyTokens() {
            monthlyTokensUsed.set(0);
            monthStartDate = LocalDateTime.now();
        }

        public int getRequestsThisMinute() {
            return requestsThisMinute.get();
        }

        public void incrementRequests() {
            requestsThisMinute.incrementAndGet();
        }

        public void resetRateLimit() {
            requestsThisMinute.set(0);
        }

        public LocalDateTime getLastRequestTime() {
            return lastRequestTime;
        }

        public void setLastRequestTime(LocalDateTime lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
        }

        public LocalDateTime getMonthStartDate() {
            return monthStartDate;
        }

        public List<DailyUsage> getUsageHistory() {
            return usageHistory;
        }

        public void addDailyUsage(DailyUsage usage) {
            usageHistory.add(usage);
            if (usageHistory.size() > 7) {
                usageHistory.remove(0);
            }
        }
    }

    public static class DailyUsage {
        private final LocalDate date;
        private final int tokensUsed;
        private final int requestsCount;

        public DailyUsage(int tokensUsed, int requestsCount) {
            this.date = LocalDate.now();
            this.tokensUsed = tokensUsed;
            this.requestsCount = requestsCount;
        }

        public LocalDate getDate() {
            return date;
        }

        public int getTokensUsed() {
            return tokensUsed;
        }

        public int getRequestsCount() {
            return requestsCount;
        }
    }
}