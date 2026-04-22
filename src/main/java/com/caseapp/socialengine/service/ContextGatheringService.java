package com.caseapp.socialengine.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.caseapp.socialengine.dto.LiveContext;
import com.caseapp.socialengine.dto.WeatherResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContextGatheringService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${case.target.city}")
    private String targetCity;

    public boolean checkNationalContext() {
    String newsUrl = "https://newsapi.org/v2/top-headlines?country=in&apiKey=315970c467bb4b1ca31c71b9948fdbbe";
    // If news contains keywords like "Crisis", "Emergency", "Tragedy"
    // Return FALSE to halt marketing posts immediately.
    return true; 
}

    public LiveContext fetchCurrentWorldContext() {
        log.info("Gathering live context from external APIs for city: {}", targetCity);
        LiveContext context = new LiveContext();
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. Build the exact URL for the HTTP call
            String finalUrl = apiUrl.replace("{city}", targetCity).replace("{apiKey}", apiKey);

            // 2. Make the live API call
            WeatherResponse response = restTemplate.getForObject(finalUrl, WeatherResponse.class);

            // 3. Extract the weather condition and capitalize it to match our Drools rules (e.g., "RAIN")
            if (response != null && response.getWeather() != null && !response.getWeather().isEmpty()) {
                String liveWeather = response.getWeather().get(0).getMain().toUpperCase();
                context.setCurrentWeather(liveWeather);
                log.info("LIVE WEATHER DETECTED: {}", liveWeather);
            } else {
                context.setCurrentWeather("UNKNOWN");
            }

        } catch (Exception e) {
            log.error("Failed to fetch live weather data. Defaulting to CLEAR.", e);
            context.setCurrentWeather("CLEAR"); // Fallback so the engine doesn't crash
        }
        

        // Simulating the news threat API for now
        context.setThreateningEventActive(false); 
        context.setTrendingSentiment("NEUTRAL");

        return context;
    }
}