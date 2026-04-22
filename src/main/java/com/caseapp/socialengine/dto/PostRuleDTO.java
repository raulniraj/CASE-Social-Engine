package com.caseapp.socialengine.dto;

import lombok.Data;

@Data
public class PostRuleDTO {
    private String ruleType; 
    private String conditionValue; 
    private String action; 
}