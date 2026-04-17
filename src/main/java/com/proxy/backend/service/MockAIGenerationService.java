package com.proxy.backend.service;

import com.proxy.backend.dto.GenerationRequest;
import com.proxy.backend.dto.GenerationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MockAIGenerationService implements AIGenerationService {

    private static final List<String> RESPONSES = List.of(
        "Para una dieta saludable, te recomiendo incluir muchas verduras, frutas frescas, proteínas magras y granos integrales. Evita alimentos ultraprocesados y reduce el consumo de azúcar y sal.",
        "El futuro de la inteligencia artificial se ve muy prometedor, con avances en procesamiento de lenguaje natural que llevan a interacciones más intuitivas entre humanos y computadoras.",
        "Los algoritmos de aprendizaje automático pueden analizar grandes cantidades de datos en segundos, permitiendo la toma de decisiones en tiempo real en diversas industrias.",
        "Las redes neuronales han evolucionado para imitar las funciones del cerebro humano, creando sistemas capaces de resolución creativa de problemas y aprendizaje adaptativo.",
        "Los modelos de aprendizaje profundo están revolucionando la salud al permitir la detección temprana de enfermedades mediante el reconocimiento de patrones en datos médicos.",
        "Los sistemas autónomos impulsados por IA están transformando el transporte, la logística y los sectores de manufactura a nivel mundial.",
        "La generación de lenguaje natural ha alcanzado un punto donde la IA puede producir artículos coherentes y relevantes sobre casi cualquier tema.",
        "La visión por computadora combinada con aprendizaje automático está haciendo que los vehículos autónomos sean más seguros que los conductores humanos.",
        "Los motores de personalización impulsados por IA están remodelando el comercio electrónico ofreciendo experiencias adaptadas según los patrones de comportamiento.",
        "La computación cuántica promete acelerar las capacidades de IA por órdenes de magnitud, resolviendo problemas actualmente fuera del alcance de las computadoras clásicas.",
        "Una dieta balanceada debe incluir: proteínas (pollo, pescado, huevos), carbohidratos complejos (arroz integral, avena), grasas saludables (aguacate, nueces) y mucha agua diaria.",
        "Para perder peso de manera saludable, combina una alimentación equilibrada con ejercicio regular. Limita las porciones, come despacio y evita comer después de las 8pm.",
        "El ejercicio diario no necesita ser intenso. 30 minutos de caminata, natación o ciclismo ligero son suficientes para mantenerte en forma."
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