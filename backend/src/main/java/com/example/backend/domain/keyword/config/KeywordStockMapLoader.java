package com.example.backend.domain.keyword.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// config/KeywordStockMapLoader.java
@Component
public class KeywordStockMapLoader {
    private final Map<String, List<String>> keywordToStocks = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, List<String>>> typeRef = new TypeReference<>() {};
        InputStream input = new ClassPathResource("keyword_stock_map.json").getInputStream();
        Map<String, List<String>> stockToKeywords = mapper.readValue(input, typeRef);

        // 키워드 → 종목으로 변환
        for (Map.Entry<String, List<String>> entry : stockToKeywords.entrySet()) {
            String stock = entry.getKey();
            for (String keyword : entry.getValue()) {
                keywordToStocks.computeIfAbsent(keyword, k -> new ArrayList<>()).add(stock);
            }
        }
    }

    public List<String> getStocksByKeyword(String keyword) {
        return keywordToStocks.getOrDefault(keyword, List.of());
    }
}
