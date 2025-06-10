package com.example.backend.domain.term.service;

import com.example.backend.domain.term.entity.Term;

import com.example.backend.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.time.LocalDate.*;

@Service
@RequiredArgsConstructor
public class TermService {
    private final TermRepository termRepository;

    public List<Term> searchTerms(String keyword) {
        return termRepository.searchByTermOrRelatedTerms(keyword);
    }
    public List<Term> getRandomTerms() {
        return termRepository.findRandomTerms();
    }
    public List<Term> getDailyTerms() {
        List<Term> allTerms = termRepository.findAll();
        long seed = now().toEpochDay();  // 날짜 기반 seed
        Collections.shuffle(allTerms, new Random(seed));

        // 최대 10개만 반환
        return allTerms.stream().limit(10).toList();
    }
}