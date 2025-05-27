package com.example.backend.domain.stock.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

@Service
public class StockService {

    private final OkHttpClient client = new OkHttpClient();

    @Value("${alphavantage.apikey}")
    private String apiKey;

    public JsonObject getRawStockData(String symbol) throws IOException {
        String url = String.format(
                "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                symbol, apiKey
        );

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            return JsonParser.parseString(json).getAsJsonObject();
        }
    }

    public Map<LocalDate, Double> getClosePriceSeries(String symbol) throws IOException {
        JsonObject json = getRawStockData(symbol);
        JsonObject series = json.getAsJsonObject("Time Series (Daily)");

        Map<LocalDate, Double> result = new TreeMap<>();  // 날짜 오름차순
        for (String dateStr : series.keySet()) {
            double close = series.getAsJsonObject(dateStr).get("4. close").getAsDouble();
            result.put(LocalDate.parse(dateStr), close);
        }
        return result;
    }

}
