package com.example.backend.domain.term.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Table(name = "Term")
@EntityListeners(EntityListeners.class)
public class Term {
    @Id
    private int id ;
    private String word;
    private String definition;
    private String category;
    @CreatedDate
    private LocalDateTime createdAt;

}
