package com.example.backend.domain.keyword.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Keyword {

    @Id
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "keywords")
    private Set<Company> companies = new HashSet<>();
}
