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
    public ResponseEntity<?> generate(@Valid @RequestBody GenerationRequest request,
            @RequestParam(defaultValue = "default") String userId) {
        try {
            GenerationResponse response = proxyChainService.generate(userId, request);
            return ResponseEntity.ok(response);
        } catch (RateLimitExceededException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Rate limit exceeded");
            body.put("retryAfter", e.getRetryAfterSeconds());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", String.valueOf(e.getRetryAfterSeconds()))
                    .body(body);
        } catch (QuotaExceededException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Monthly quota exhausted");
            body.put("upgrade", true);
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(body);
        }
    }

    @GetMapping("/quota/status")
    public ResponseEntity<Map<String, Object>> getQuotaStatus(
            @RequestParam(defaultValue = "default") String userId) {
        UserContext.UserState userState = userContext.getOrCreateUser(userId);
        Plan plan = userState.getPlan();
        int used = userState.getMonthlyTokensUsed();
        int total = plan.getMonthlyTokens();
        int requestsPerMinute = plan.getRequestsPerMinute();
        int requestsUsed = userState.getRequestsThisMinute();

        Map<String, Object> response = new HashMap<>();
        response.put("tokensUsed", used);
        response.put("tokensRemaining", total == Integer.MAX_VALUE ? "unlimited" : total - used);
        response.put("resetDate", getNextResetDate());
        response.put("plan", plan.name());
        response.put("requestsPerMinute", requestsPerMinute == Integer.MAX_VALUE ? "unlimited" : requestsPerMinute);
        response.put("requestsUsed", requestsUsed);
        response.put("requestsRemaining", requestsPerMinute == Integer.MAX_VALUE ? "unlimited" : requestsPerMinute - requestsUsed);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/quota/history")
    public ResponseEntity<List<Map<String, Object>>> getQuotaHistory(
            @RequestParam(defaultValue = "default") String userId) {
        UserContext.UserState userState = userContext.getOrCreateUser(userId);
        
        List<Map<String, Object>> history = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("tokensUsed", 0);
            dayData.put("requestsCount", 0);
            history.add(dayData);
        }
        
        return ResponseEntity.ok(history);
    }

    @PostMapping("/quota/select-plan")
    public ResponseEntity<Map<String, Object>> selectPlan(@RequestBody Map<String, String> request,
            @RequestParam(defaultValue = "default") String userId) {
        String planName = request.get("plan");
        Plan newPlan;
        try {
            newPlan = Plan.valueOf(planName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid plan. Use FREE, PRO, or ENTERPRISE"));
        }
        
        UserContext.UserState userState = userContext.getOrCreateUser(userId);
        Plan currentPlan = userState.getPlan();
        userState.setPlan(newPlan);

        Map<String, Object> response = new HashMap<>();
        response.put("previousPlan", currentPlan.name());
        response.put("currentPlan", newPlan.name());
        response.put("message", "Plan changed successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/quota/upgrade")
    public ResponseEntity<Map<String, Object>> upgradePlan(
            @RequestParam(defaultValue = "default") String userId) {
        UserContext.UserState userState = userContext.getOrCreateUser(userId);
        Plan currentPlan = userState.getPlan();

        Plan newPlan = switch (currentPlan) {
            case FREE -> Plan.PRO;
            case PRO -> Plan.ENTERPRISE;
            case ENTERPRISE -> Plan.ENTERPRISE;
        };
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