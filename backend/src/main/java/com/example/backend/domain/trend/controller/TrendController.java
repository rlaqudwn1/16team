package com.example.backend.domain.trend.controller;

import com.example.backend.domain.keyword.service.RecommendationService;
import com.example.backend.domain.trend.service.TrendService;
import com.example.backend.domain.trend.dto.TrendDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

// controller/TrendController.java
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/trends")
@RequiredArgsConstructor
public class TrendController {
    private final TrendService trendService;
    private final RecommendationService recommend;

    private static final Logger log = LoggerFactory.getLogger(TrendController.class);

    @PostMapping(
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8"
    )    public ResponseEntity<String> receiveTrends(@RequestBody List<TrendDTO> trends) {
        recommend.recommendByTrend(trends.stream().flatMap(dto -> dto.getKeywords().stream())
                .distinct().toList());

        trendService.saveTrends(trends);
        return ResponseEntity.ok("✅ Trends successfully received!");
    }
}
