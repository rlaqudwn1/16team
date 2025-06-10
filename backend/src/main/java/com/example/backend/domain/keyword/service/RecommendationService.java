package com.example.backend.domain.keyword.service;

import com.example.backend.domain.keyword.config.KeywordStockMapLoader;
import com.example.backend.domain.keyword.dto.RecommendationResponseDto;
import com.example.backend.domain.keyword.entity.KeywordEntity;
import com.example.backend.domain.keyword.repoistory.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// service/RecommendationService.java
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);
    private final KeywordRepository keywordRepository;



    public List<RecommendationResponseDto> recommendByTrend(List<String> trendKeywords) {
        var matched = keywordRepository.findByKeywordIn(trendKeywords);
        log.info("추천 요청 키워드 : {}", trendKeywords);
        trendKeywords.get(0);
        // 키워드별로 그룹화
        Map<String, List<String>> grouped = matched.stream()
                .collect(Collectors.groupingBy(
                        KeywordEntity::getKeyword,
                        Collectors.mapping(KeywordEntity::getStockName, Collectors.toList())
                ));
        log.info(" DB 매칭 결과: {} ", matched);

        return grouped.entrySet().stream()
                .map(e -> new RecommendationResponseDto(e.getKey(), e.getValue()))
                .toList();
    }
}

