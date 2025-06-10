package com.example.backend.domain.trend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
public class TrendDTO {
    private int id;
    private List<String> keywords;
    private String category;
    private String createdAt;
}

