package com.example.backend.domain.term.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "term")
@EntityListeners(AuditingEntityListener.class)
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String term;

    @Column(columnDefinition = "TEXT")
    private String definition;

    private String category;

    private String english;

    @ElementCollection
    @CollectionTable(name = "term_related_terms", joinColumns = @JoinColumn(name = "term_id"))
    @Column(name = "related_term")
    @JsonProperty("related_terms")  // ✅ JSON의 필드명을 명시적으로 지정
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<String> relatedTerms = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String example;

}
