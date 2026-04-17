package com.proxy.backend.service;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;
import com.proxy.backend.exception.QuotaExceededException;
import com.proxy.backend.model.Plan;
import com.proxy.backend.model.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class QuotaProxyService implements AIGenerationService {

    private final UserContext userContext;
    private AIGenerationService nextService;
    private String currentUserId = "default";

    public QuotaProxyService(UserContext userContext) {
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
        int monthlyLimit = plan.getMonthlyTokens();

        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.MONTHS.between(userState.getMonthStartDate(), now) >= 1) {
            userState.resetMonthlyTokens();
        }

        int tokensUsed = userState.getMonthlyTokensUsed();

        if (monthlyLimit != Integer.MAX_VALUE && tokensUsed >= monthlyLimit) {
            throw new QuotaExceededException("Monthly quota exhausted");
        }

        GenerationResponse response = nextService.generate(request);
        userState.addTokens(response.getTokensUsed());

        return response;
    }
}