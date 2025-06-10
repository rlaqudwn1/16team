package com.example.backend.domain.news.controller;

import com.example.backend.domain.news.entity.News;
import com.example.backend.domain.news.entity.NewsRequestDto;
import com.example.backend.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<?> receiveNews(@RequestBody List<NewsRequestDto> newsList) {
        List<News> saved = newsService.saveAll(newsList);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/all")
    public ResponseEntity<List<News>> getAllNews() {
        List<News> newsList = newsService.findAll();
        return ResponseEntity.ok(newsList);
    }

}

