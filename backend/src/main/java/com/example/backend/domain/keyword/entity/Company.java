package com.example.backend.domain.keyword.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Company {

    @Id
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "company_keyword_mapping",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();
}
