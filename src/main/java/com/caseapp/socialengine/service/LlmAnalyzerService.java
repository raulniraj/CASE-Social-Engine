package com.caseapp.socialengine.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.caseapp.socialengine.dto.LlmResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LlmAnalyzerService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public LlmResponse analyzePostContext(String postContent, String liveWeather, String liveNews) {
        log.info("Sending post to AI for context analysis...");
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // 1. Craft the ultimate prompt
            String prompt = "You are an autonomous marketing AI. Analyze this post: \"" + postContent + "\"\n" +
            "Current Real-World Context:\n" +
            "- Live Weather: " + liveWeather + "\n" +
            "- Current Headlines: " + liveNews + "\n\n" +
            "DECISION RULES:\n" +
            "1. If the news contains a CRISIS, DISASTER, or NATIONAL TRAGEDY, set status to 'BLOCK'.\n" +
            "2. If the post mentions weather but the live weather doesn't match (e.g. posting about rain when it is sunny), set status to 'HOLD'.\n" +
            "3. Otherwise, set status to 'PROCEED'.\n\n" +
            "Respond strictly in JSON: {\"status\": \"BLOCK/HOLD/PROCEED\", \"reason\": \"Why?\", \"suggestedContent\": \"New text if needed\"}";

        try {
            // 2. Build the JSON safely using Java Maps (Enterprise standard)
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> partsMap = new HashMap<>();
            partsMap.put("parts", Arrays.asList(textPart));

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("contents", Arrays.asList(partsMap));

            String requestBody = mapper.writeValueAsString(requestMap);

            // 3. Make the call
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            String finalUrl = apiUrl + apiKey;
            String response = restTemplate.postForObject(finalUrl, request, String.class);

            // 4. Parse the response
            JsonNode rootNode = mapper.readTree(response);
            String aiText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            aiText = aiText.replace("```json", "").replace("```", "").trim();
            log.info("AI Analysis Complete: \n{}", aiText);
            
            return mapper.readValue(aiText, LlmResponse.class);

        }  catch (Exception e) {
    log.error("AI Analysis failed! Error: {}", e.getMessage());
    LlmResponse fallback = new LlmResponse();
    fallback.setStatus("NONE"); // FIXED
    fallback.setReason("NONE"); // FIXED
    fallback.setSuggestedContent(postContent);
    return fallback;
}
    }
}