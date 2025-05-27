package com.example.backend.domain.stock.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class ChartRenderer {

    public static byte[] renderLineChart(String title, Map<LocalDate, Double> priceData) throws IOException {
        XYSeries series = new XYSeries("Close Price");

        int x = 0;
        for (Map.Entry<LocalDate, Double> entry : priceData.entrySet()) {
            series.add(x++, entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Days",
                "Price (USD)",
                dataset
        );

        BufferedImage image = chart.createBufferedImage(600, 400);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(baos, image);
        return baos.toByteArray();
    }
}
