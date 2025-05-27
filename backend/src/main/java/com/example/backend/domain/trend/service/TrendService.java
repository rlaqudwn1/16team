package com.example.backend.domain.trend.service;

// service/TrendService.java
import com.example.backend.domain.trend.entitiy.TrendEntity;
import com.example.backend.domain.trend.repository.TrendRepository;
import com.example.backend.domain.trend.dto.TrendDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final TrendRepository trendRepository;

    public void saveTrends(List<TrendDTO> trendDTOs) {
        List<TrendEntity> entities = trendDTOs.stream()
                .map(dto -> TrendEntity.builder()
                        .query(dto.getQuery())
                        .searchVolume(dto.getSearchVolume())
                        .relatedKeywords(dto.getRelatedKeywords())
                        .categories(dto.getCategories())
                        .link(dto.getLink())
                        .build())
                .toList();

        trendRepository.saveAll(entities);
    }
}

