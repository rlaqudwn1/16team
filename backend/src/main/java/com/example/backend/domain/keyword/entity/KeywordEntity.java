package com.example.backend.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.Data;

// entity/KeywordEntity.java
@Entity
@Table(name = "keywords")
@Data
public class KeywordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private String stockName; // 연관된 주식 회사 이름

    // 생성자, getter/setter 생략
}
