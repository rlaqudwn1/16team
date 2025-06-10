package com.example.backend.domain.trend.repository;

import com.example.backend.domain.trend.entitiy.TrendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendRepository extends JpaRepository<TrendEntity, Long> {
}