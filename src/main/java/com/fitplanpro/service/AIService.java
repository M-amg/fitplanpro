package com.fitplanpro.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.MessageParam;
import com.anthropic.models.messages.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitplanpro.dto.aiservice.AIPromptTemplateDto;
import com.fitplanpro.dto.aiservice.AIServiceRequestDto;
import com.fitplanpro.dto.aiservice.AIServiceResponseDto;
import com.fitplanpro.exception.AIServiceException;
import com.fitplanpro.repository.AIPromptTemplateRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for AI operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final OpenAiService openAiService;
    private final AnthropicClient anthropicClient;
    private final AIPromptTemplateRepository promptTemplateRepository;
    private final ObjectMapper objectMapper;

    @Value("${fitplan.ai.openai.model}")
    private String openAiModel;

    @Value("${fitplan.ai.claude.model}")
    private String claudeModel;

    @Value("${fitplan.ai.cache.enabled:true}")
    private boolean cacheEnabled;

    /**
     * Generate content using AI
     *
     * @param requestDto the AI service request
     * @return the AI service response
     */
    @Cacheable(value = "aiResponses", key = "#requestDto.toString()", condition = "${fitplan.ai.cache.enabled:true}")
    public AIServiceResponseDto generateContent(AIServiceRequestDto requestDto) {
        // Get template
        AIPromptTemplateDto template = promptTemplateRepository
                .findTopByTemplateTypeAndIsActiveTrueOrderByVersionDesc(requestDto.getTemplateType())
                .map(t -> new AIPromptTemplateDto(
                        t.getId(),
                        t.getTemplateName(),
                        t.getTemplateType(),
                        t.getTemplateContent(),
                        t.getVersion(),
                        t.getIsActive(),
                        t.getCreatedAt(),
                        t.getUpdatedAt()
                ))
                .orElseThrow(() -> new AIServiceException("No active template found for type: " + requestDto.getTemplateType()));

        // Create prompt
        String prompt = createPrompt(template.getTemplateContent(), requestDto.getParameters());

        // Generate content
        long startTime = System.currentTimeMillis();
        Map response;
        String modelUsed;

        try {
            // Check if a specific model is requested
            if (requestDto.getModelPreference() != null) {
                if (requestDto.getModelPreference().toLowerCase().contains("claude")) {
                    response = generateWithClaude(prompt, requestDto);
                    modelUsed = claudeModel;
                } else {
                    response = generateWithOpenAI(prompt, requestDto);
                    modelUsed = openAiModel;
                }
            } else {
                // Default to OpenAI
                response = generateWithOpenAI(prompt, requestDto);
                modelUsed = openAiModel;
            }
        } catch (Exception e) {
            log.error("Error generating content with primary model, trying fallback", e);
            try {
                // Fallback to Claude if OpenAI fails, or vice versa
                if (requestDto.getModelPreference() != null &&
                        requestDto.getModelPreference().toLowerCase().contains("claude")) {
                    response = generateWithOpenAI(prompt, requestDto);
                    modelUsed = openAiModel;
                } else {
                    response = generateWithClaude(prompt, requestDto);
                    modelUsed = claudeModel;
                }
            } catch (Exception fallbackEx) {
                log.error("Error generating content with fallback model", fallbackEx);
                throw new AIServiceException("Failed to generate content with both models", fallbackEx);
            }
        }

        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;

        // Create cache key
        String cacheKey = createCacheKey(requestDto);

        // Return response
        return AIServiceResponseDto.builder()
                .modelUsed(modelUsed)
                .response(response)
                .processingTimeMs(processingTime)
                .fromCache(false)
                .cacheKey(cacheKey)
                .build();
    }

    /**
     * Generate content with OpenAI
     *
     * @param prompt the prompt
     * @param requestDto the request DTO
     * @return the response
     */
    private Map<String, Object> generateWithOpenAI(String prompt, AIServiceRequestDto requestDto) {
        try {
            // Parse prompt JSON
            Map<String, Object> promptMap = objectMapper.readValue(prompt, Map.class);

            // Extract system prompt
            String systemPrompt = (String) promptMap.get("system_prompt");

            // Extract user context
            Map<String, Object> userContext = (Map<String, Object>) promptMap.get("user_context");
            String userPrompt = objectMapper.writeValueAsString(userContext);

            // Create chat messages
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), userPrompt));

            // Create completion request
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(openAiModel)
                    .messages(messages)
                    .maxTokens(requestDto.getMaxTokens())
                    .temperature(requestDto.getTemperature())
                    .build();

            // Get completion
            String completion = openAiService.createChatCompletion(completionRequest)
                    .getChoices().getFirst().getMessage().getContent();

            // Parse completion JSON
            return objectMapper.readValue(completion, Map.class);
        } catch (JsonProcessingException e) {
            throw new AIServiceException("Error processing JSON with OpenAI", e);
        }
    }

    /**
     * Generate content with Claude
     *
     * @param prompt     the prompt
     * @param requestDto the request DTO
     * @return the response
     */
    private Map<String, Object> generateWithClaude(String prompt, AIServiceRequestDto requestDto) {
        try {
            // Parse prompt JSON
            Map<String, Object> promptMap = objectMapper.readValue(prompt, Map.class);

            // Extract system prompt
            String systemPrompt = (String) promptMap.get("system_prompt");

            // Extract user context
            Map<String, Object> userContext = (Map<String, Object>) promptMap.get("user_context");
            String userPrompt = objectMapper.writeValueAsString(userContext);

            // Create message params
            MessageCreateParams messageParams = MessageCreateParams.builder()
                    .model(getClaudeModelEnum(claudeModel))
                    .maxTokens(requestDto.getMaxTokens())
                    .temperature(requestDto.getTemperature())
                    .system(systemPrompt)
                    .messages(Collections.singletonList(MessageParam.builder()
                            .role(MessageParam.Role.USER)
                            .content(userPrompt)
                            .build()))
                    .build();

            // Get completion
            Message messageResponse = anthropicClient.messages().create(messageParams);
            String completion = String.valueOf(messageResponse.content().getFirst().text());

            // Parse completion JSON
            return objectMapper.readValue(completion, Map.class);
        } catch (JsonProcessingException e) {
            throw new AIServiceException("Error processing JSON with Claude", e);
        }
    }

    /**
     * Convert string model name to Model enum
     *
     * @param modelName the model name string
     * @return the Model enum
     */
    private Model getClaudeModelEnum(String modelName) {
        return switch (modelName.toLowerCase()) {
            case "claude-3-haiku-20240307" -> Model.CLAUDE_3_HAIKU_20240307;
            case "claude-3-5-sonnet-20241022" -> Model.CLAUDE_3_5_SONNET_20241022;
            case "claude-3-5-haiku-20241022" -> Model.CLAUDE_3_5_HAIKU_20241022;
            case "claude-sonnet-4-20250514" -> Model.CLAUDE_SONNET_4_20250514;
            case "claude-opus-4-1-20250805" -> Model.CLAUDE_OPUS_4_1_20250805;
            default -> {
                log.warn("Unknown Claude model: {}, using default CLAUDE_3_SONNET_20240229", modelName);
                yield Model.CLAUDE_3_OPUS_20240229;
            }
        };
    }

    /**
     * Create prompt by replacing parameters in template
     *
     * @param templateContent the template content
     * @param parameters the parameters
     * @return the prompt
     */
    private String createPrompt(String templateContent, Map<String, Object> parameters) {
        String prompt = templateContent;

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";

            if (entry.getValue() != null) {
                prompt = prompt.replace(placeholder, entry.getValue().toString());
            } else {
                prompt = prompt.replace(placeholder, "");
            }
        }

        return prompt;
    }

    /**
     * Create cache key for AI service request
     *
     * @param requestDto the request DTO
     * @return the cache key
     */
    private String createCacheKey(AIServiceRequestDto requestDto) {
        try {
            // Create map with key fields
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("templateType", requestDto.getTemplateType());
            keyMap.put("parameters", requestDto.getParameters());
            keyMap.put("maxTokens", requestDto.getMaxTokens());
            keyMap.put("temperature", requestDto.getTemperature());

            // Convert to string
            return objectMapper.writeValueAsString(keyMap);
        } catch (JsonProcessingException e) {
            log.error("Error creating cache key", e);
            return requestDto.toString();
        }
    }
}