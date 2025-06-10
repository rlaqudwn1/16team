package com.example.backend.domain.news.repository;

import com.example.backend.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByLink(String link); // 중복 방지용
}
