package com.caseapp.socialengine.dto;

import lombok.Data;
import java.util.List;

@Data
public class WeatherResponse {
    private List<Weather> weather;

    @Data
    public static class Weather {
        // This will capture "Rain", "Clear", "Clouds", etc.
        private String main; 
    }
}