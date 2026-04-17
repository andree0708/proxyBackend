package com.proxy.backend.service;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class ProxyChainService {

    private final RateLimitProxyService rateLimitProxy;
    private final QuotaProxyService quotaProxy;

    public ProxyChainService(RateLimitProxyService rateLimitProxy, 
                             QuotaProxyService quotaProxy) {
        this.rateLimitProxy = rateLimitProxy;
        this.quotaProxy = quotaProxy;
    }

    @PostConstruct
    public void init() {
        MockAIGenerationService mockService = new MockAIGenerationService();
        quotaProxy.setNextService(mockService);
        rateLimitProxy.setNextService(quotaProxy);
    }

    public GenerationResponse generate(String userId, GenerationRequest request) {
        rateLimitProxy.setUserId(userId);
        quotaProxy.setUserId(userId);
        return rateLimitProxy.generate(request);
    }
}