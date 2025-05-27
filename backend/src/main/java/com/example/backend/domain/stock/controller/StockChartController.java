package com.example.backend.domain.stock.controller;

import com.example.backend.domain.stock.service.StockService;
import com.example.backend.domain.stock.util.ChartRenderer;
import com.google.gson.JsonObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/stocks")
public class StockChartController {

    private final StockService stockService;

    public StockChartController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/data")
    public ResponseEntity<String> getStockData(@RequestParam("symbol") String symbol) {
        try {
            JsonObject data = stockService.getRawStockData(symbol);
            return ResponseEntity.ok(data.toString());  // ✅ JSON 문자열로 응답
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터 조회 실패: " + e.getMessage());
        }
    }
    @GetMapping("/chart")
    public ResponseEntity<byte[]> getStockChart(@RequestParam("symbol") String symbol) {
        try {
            Map<LocalDate, Double> closePrices = stockService.getClosePriceSeries(symbol);
            byte[] imageBytes = ChartRenderer.renderLineChart("Stock Price: " + symbol, closePrices);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
