package com.example.backend.trend.entitiy;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "trend")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String query;

    private Integer searchVolume;

    @ElementCollection
    @CollectionTable(name = "trend_keywords", joinColumns = @JoinColumn(name = "trend_id"))
    @Column(name = "keyword")
    private List<String> relatedKeywords;

    @ElementCollection
    @CollectionTable(name = "trend_categories", joinColumns = @JoinColumn(name = "trend_id"))
    @Column(name = "category")
    private List<String> categories;

    private String link;
}

