package com.example.backend.domain.term.controller;

import com.example.backend.domain.term.entity.Term;
import com.example.backend.domain.term.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermController {
    private final TermService termService;

    @GetMapping("/search")
    public List<Term> searchTerms(@RequestParam("keyword") String keyword) {
        return termService.searchTerms(keyword);
    }

    @GetMapping("/daily")
    public List<Term> getDailyTerms() {
        return termService.getDailyTerms();
    }
}
