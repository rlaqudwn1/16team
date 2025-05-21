package com.example.backend.trend.repository;

import com.example.backend.trend.entitiy.TrendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendRepository extends JpaRepository<TrendEntity, Long> {
}