package com.example.backend.domain.term.util;


import com.example.backend.domain.term.entity.Term;
import com.example.backend.domain.term.repository.TermRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TermLoader implements CommandLineRunner {

    private final TermRepository termRepository;
    private final ObjectMapper objectMapper;


    @Override
    public void run(String... args) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/terms.json");
        List<Term> terms = objectMapper.readValue(inputStream, new TypeReference<List<Term>>() {});

        for (Term term : terms) {
            if (!termRepository.existsByTerm(term.getTerm())) {
                termRepository.save(term);
            }
        }
    }

}
