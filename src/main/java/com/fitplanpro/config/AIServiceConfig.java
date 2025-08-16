package com.fitplanpro.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
public class AIServiceConfig {

    @Value("${fitplan.ai.openai.api-key:}")
    private String openAiApiKey;

    @Value("${fitplan.ai.openai.timeout:30}")
    private int openAiTimeoutSeconds;

    @Value("${fitplan.ai.claude.api-key:}")
    private String claudeApiKey;

    @Value("${fitplan.ai.claude.base-url:https://api.anthropic.com}")
    private String claudeBaseUrl;

    @Bean
    @Primary
    public OpenAiService openAiService() {
        if (!StringUtils.hasText(openAiApiKey)) {
            throw new IllegalStateException("OpenAI API key is required");
        }
        return new OpenAiService(openAiApiKey, Duration.ofSeconds(openAiTimeoutSeconds));
    }

    @Bean
    public AnthropicClient anthropicClient() {
        if (!StringUtils.hasText(claudeApiKey)) {
            throw new IllegalStateException("Claude API key is required");
        }

        return AnthropicOkHttpClient.builder()
                .apiKey(claudeApiKey)
                .baseUrl(claudeBaseUrl)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}