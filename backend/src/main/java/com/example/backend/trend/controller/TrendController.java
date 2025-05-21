package com.example.backend.trend.controller;

import com.example.backend.trend.dto.TrendDTO;
import com.example.backend.trend.service.TrendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

// controller/TrendController.java
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/trends")
@RequiredArgsConstructor
public class TrendController {
    private final TrendService trendService;

    private static final Logger log = LoggerFactory.getLogger(TrendController.class);

    @PostMapping(
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8"
    )    public ResponseEntity<String> receiveTrends(@RequestBody List<TrendDTO> trends) {
        // TODO: DB 저장 또는 로그 확인
        trendService.saveTrends(trends);
        return ResponseEntity.ok("✅ Trends successfully received!");
    }
}
