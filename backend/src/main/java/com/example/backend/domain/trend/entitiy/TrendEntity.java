package com.example.backend.domain.trend.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trends")
@Data
@Builder
public class TrendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @ElementCollection
    @CollectionTable(name = "trend_keywords", joinColumns = @JoinColumn(name = "trend_id"))
    @Column(name = "keyword")
    private List<String> keywords;
    @CreatedDate
    private LocalDateTime collectedAt;
}


