package com.example.backend.domain.news.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequestDto {

    private String title;
    private String link;
    private String source;
    private String publishedAt;
    private String snippet;
    private String content;
    private String gptSummary;
    private String thumbnail;
}
