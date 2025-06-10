package com.example.backend.domain.trend.service;

// service/TrendService.java
import com.example.backend.domain.trend.entitiy.TrendEntity;
import com.example.backend.domain.trend.repository.TrendRepository;
import com.example.backend.domain.trend.dto.TrendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendService {

    private final TrendRepository trendRepository;

    public void saveTrends(List<TrendDTO> trendDTOs) {

        log.info("categoryTest"+trendDTOs.get(0).getCategory());
        List<TrendEntity> entities = trendDTOs.stream()
                .map(dto -> TrendEntity.builder()
                        .keywords(dto.getKeywords().stream().toList())
                        .category(dto.getCategory())
                        .collectedAt(LocalDateTime.now())
                        .build())
                .toList();

        trendRepository.saveAll(entities);
    }
}

