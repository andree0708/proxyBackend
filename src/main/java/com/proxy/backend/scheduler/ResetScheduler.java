package com.proxy.backend.scheduler;

import com.proxy.backend.model.UserContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResetScheduler {

    private final UserContext userContext;

    public ResetScheduler(UserContext userContext) {
        this.userContext = userContext;
    }

    @Scheduled(cron = "0 * * * * *")
    public void resetRateLimit() {
        userContext.getOrCreateUser("default").resetRateLimit();
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyQuota() {
        userContext.getOrCreateUser("default").resetMonthlyTokens();
    }
}