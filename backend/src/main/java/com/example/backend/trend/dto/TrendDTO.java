package com.example.backend.trend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
public class TrendDTO {
    private String query;
    private Integer searchVolume;
    private List<String> relatedKeywords;
    private List<String> categories;
    private String link;
}

