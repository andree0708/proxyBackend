package com.proxy.backend.service;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;
import com.proxy.backend.exception.RateLimitExceededException;
import com.proxy.backend.model.Plan;
import com.proxy.backend.model.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RateLimitProxyService implements AIGenerationService {

    private final UserContext userContext;
    private AIGenerationService nextService;
    private String currentUserId = "default";

    public RateLimitProxyService(UserContext userContext) {
        this.userContext = userContext;
    }

    public void setNextService(AIGenerationService nextService) {
        this.nextService = nextService;
    }

    public void setUserId(String userId) {
        this.currentUserId = userId;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserContext.UserState userState = userContext.getOrCreateUser(currentUserId);
        Plan plan = userState.getPlan();
        int limit = plan.getRequestsPerMinute();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequest = userState.getLastRequestTime();

        if (lastRequest != null && ChronoUnit.MINUTES.between(lastRequest, now) >= 1) {
            userState.resetRateLimit();
        }

        int currentRequests = userState.getRequestsThisMinute();

        if (limit != Integer.MAX_VALUE && currentRequests >= limit) {
            int retryAfter = 60 - LocalDateTime.now().getSecond();
            throw new RateLimitExceededException("Rate limit exceeded", retryAfter);
        }

        userState.setLastRequestTime(now);
        userState.incrementRequests();

        return nextService.generate(request);
    }
}