package com.caseapp.socialengine.dto;

import lombok.Data;

@Data
public class LlmResponse {
    private String status;           // Changed from dependsOn
    private String reason;           // Changed from condition
    private String suggestedContent;
}