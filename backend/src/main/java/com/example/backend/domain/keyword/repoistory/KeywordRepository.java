package com.example.backend.domain.keyword.repoistory;

import com.example.backend.domain.keyword.entity.KeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// repository/KeywordRepository.java
@Repository
public interface KeywordRepository extends JpaRepository<KeywordEntity, Long> {
    List<KeywordEntity> findByKeywordIn(List<String> keywords);
}
