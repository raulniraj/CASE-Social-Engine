package com.caseapp.socialengine.dto;

import lombok.Data;

@Data
public class LiveContext {
    private String currentWeather; // e.g., "CLEAR", "RAIN", "SNOW"
    private boolean isThreateningEventActive; // e.g., true if breaking news/disaster is happening
    private String trendingSentiment; // e.g., "POSITIVE", "NEGATIVE"
}