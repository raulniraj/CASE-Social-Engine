package com.caseapp.socialengine.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewsService {

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url}")
    private String apiUrl;

    public String getTopHeadlines() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl + apiKey, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode articles = root.path("articles");

            List<String> headlines = new ArrayList<>();
            // Just grab the top 5 headlines to avoid overwhelming the AI
            for (int i = 0; i < Math.min(articles.size(), 5); i++) {
                headlines.add(articles.get(i).path("title").asText());
            }

            String summary = String.join(" | ", headlines);
            log.info("Latest News Ingested: {}", summary);
            return summary;
        } catch (Exception e) {
            log.error("Failed to fetch news context: {}", e.getMessage());
            return "No news data available.";
        }
    }
}