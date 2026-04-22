package com.caseapp.socialengine.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostRequestDTO {
    private Long userId;
    private String content;
    private String platform; 
    private LocalDateTime scheduledTime;
    private List<PostRuleDTO> rules;
}