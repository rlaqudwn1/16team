package com.example.backend.domain.keyword.dto;

import lombok.Data;

import java.util.List;

// dto/RecommendationResponseDto.java
@Data
public class RecommendationResponseDto {
    private String keyword;
    private List<String> matchedCompanies;

    public RecommendationResponseDto(String keyword, List<String> matchedCompanies) {
        this.keyword = keyword;
        this.matchedCompanies = matchedCompanies;
    }
}
