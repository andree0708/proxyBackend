package com.proxy.backend.service;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MockAIGenerationService implements AIGenerationService {

    private static final List<String> RESPONSES = List.of(
        "The future of artificial intelligence looks remarkably promising, with breakthroughs in natural language processing leading to more intuitive human-computer interactions.",
        "Machine learning algorithms can now analyze vast amounts of data in seconds, enabling real-time decision making across industries.",
        "Neural networks have evolved to mimic human brain functions, creating systems capable of creative problem solving and adaptive learning.",
        "Deep learning models are revolutionizing healthcare by enabling early disease detection through pattern recognition in medical data.",
        "Autonomous systems powered by AI are transforming transportation, logistics, and manufacturing sectors globally.",
        "Natural language generation has reached a point where AI can produce coherent, contextually relevant articles on almost any topic.",
        "Computer vision combined with machine learning is making autonomous vehicles safer than human drivers in controlled environments.",
        "AI-powered personalization engines are reshaping e-commerce by delivering tailored experiences based on user behavior patterns.",
        "Quantum computing promises to accelerate AI capabilities by orders of magnitude, solving problems currently beyond classical computers.",
        "Reinforcement learning has enabled AI systems to master complex games and simulate environments with superhuman performance."
    );

    private final Random random = new Random();

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String responseText = RESPONSES.get(random.nextInt(RESPONSES.size()));
        int tokensUsed = estimateTokens(responseText);

        return new GenerationResponse(responseText, tokensUsed);
    }

    private int estimateTokens(String text) {
        return text.split("\\s+").length;
    }
}