package com.example.backend.domain.news.service;

import com.example.backend.domain.news.entity.News;
import com.example.backend.domain.news.entity.NewsRequestDto;
import com.example.backend.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public News save(NewsRequestDto dto) {
        if (!newsRepository.existsByLink(dto.getLink())) {
            News news = News.builder()
                    .title(dto.getTitle())
                    .link(dto.getLink())
                    .source(dto.getSource())
                    .publishedAt(dto.getPublishedAt())
                    .snippet(dto.getSnippet())
                    .content(dto.getContent())
                    .gptSummary(dto.getGptSummary())
                    .thumbnail(dto.getThumbnail())
                    .build();

            return newsRepository.save(news);
        }
        return null; // 중복일 경우 저장하지 않음
    }


    public List<News> saveAll(List<NewsRequestDto> dtos) {
        List<News> saved = new ArrayList<>();
        for (NewsRequestDto dto : dtos) {
            News news = save(dto);
            if (news != null) {
                saved.add(news);
            }
        }
        return saved;
    }
}
