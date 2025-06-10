package com.example.backend.domain.keyword.controller;

import com.example.backend.domain.keyword.dto.KeywordRequestDto;
import com.example.backend.domain.keyword.dto.RecommendationResponseDto;
import com.example.backend.domain.keyword.dto.TrendKeywordDto;
import com.example.backend.domain.keyword.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// controller/RecommendationController.java
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<List<RecommendationResponseDto>> fromTrends(
            @RequestBody TrendKeywordDto dto) {
        return ResponseEntity.ok(recommendationService.recommendByTrend(dto.getKeywords()));
    }
}

