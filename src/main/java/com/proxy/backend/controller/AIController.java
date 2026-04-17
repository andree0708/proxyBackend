package com.proxy.backend.controller;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;
import com.proxy.backend.exception.QuotaExceededException;
import com.proxy.backend.exception.RateLimitExceededException;
import com.proxy.backend.model.Plan;
import com.proxy.backend.model.UserContext;
import com.proxy.backend.service.ProxyChainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AIController {

    private final ProxyChainService proxyChainService;
    private final UserContext userContext;

    public AIController(ProxyChainService proxyChainService, UserContext userContext) {
        this.proxyChainService = proxyChainService;
        this.userContext = userContext;
    }

    @PostMapping("/ai/generate")
    public ResponseEntity<?> generate(@Valid @RequestBody GenerationRequest request) {
        try {
            GenerationResponse response = proxyChainService.generate("default", request);
            return ResponseEntity.ok(response);
        } catch (RateLimitExceededException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Rate limit exceeded");
            body.put("retryAfter", 60 - LocalDateTime.now().getSecond());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
        } catch (QuotaExceededException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Monthly quota exhausted");
            body.put("upgrade", true);
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(body);
        }
    }

    @GetMapping("/quota/status")
    public ResponseEntity<Map<String, Object>> getQuotaStatus() {
        UserContext.UserState userState = userContext.getOrCreateUser("default");
        Plan plan = userState.getPlan();
        int used = userState.getMonthlyTokensUsed();
        int total = plan.getMonthlyTokens();

        Map<String, Object> response = new HashMap<>();
        response.put("tokensUsed", used);
        response.put("tokensRemaining", total == Integer.MAX_VALUE ? "unlimited" : total - used);
        response.put("resetDate", getNextResetDate());
        response.put("plan", plan.name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/quota/history")
    public ResponseEntity<List<Map<String, Object>>> getQuotaHistory() {
        List<Map<String, Object>> history = List.of(
            Map.of("date", LocalDate.now().minusDays(6).toString(), "tokensUsed", 0, "requestsCount", 0),
            Map.of("date", LocalDate.now().minusDays(5).toString(), "tokensUsed", 0, "requestsCount", 0),
            Map.of("date", LocalDate.now().minusDays(4).toString(), "tokensUsed", 0, "requestsCount", 0),
            Map.of("date", LocalDate.now().minusDays(3).toString(), "tokensUsed", 0, "requestsCount", 0),
            Map.of("date", LocalDate.now().minusDays(2).toString(), "tokensUsed", 0, "requestsCount", 0),
            Map.of("date", LocalDate.now().minusDays(1).toString(), "tokensUsed", 0, "requestsCount", 0),
            Map.of("date", LocalDate.now().toString(), "tokensUsed", 0, "requestsCount", 0)
        );
        return ResponseEntity.ok(history);
    }

    @PostMapping("/quota/upgrade")
    public ResponseEntity<Map<String, Object>> upgradePlan() {
        UserContext.UserState userState = userContext.getOrCreateUser("default");
        Plan currentPlan = userState.getPlan();

        Plan newPlan = currentPlan == Plan.FREE ? Plan.PRO : currentPlan;
        userState.setPlan(newPlan);

        Map<String, Object> response = new HashMap<>();
        response.put("previousPlan", currentPlan.name());
        response.put("currentPlan", newPlan.name());
        response.put("message", "Plan upgraded successfully");

        return ResponseEntity.ok(response);
    }

    private String getNextResetDate() {
        LocalDate now = LocalDate.now();
        LocalDate firstOfNextMonth = now.withDayOfMonth(1).plusMonths(1);
        return firstOfNextMonth.toString();
    }
}