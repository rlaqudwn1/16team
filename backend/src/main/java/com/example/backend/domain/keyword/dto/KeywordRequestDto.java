package com.example.backend.domain.keyword.dto;

import lombok.Data;

import java.util.List;

// dto/KeywordRequestDto.java
@Data
public class KeywordRequestDto {
    private List<String> keywords;
}
