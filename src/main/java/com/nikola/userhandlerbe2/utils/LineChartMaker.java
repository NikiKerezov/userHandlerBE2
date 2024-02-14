package com.nikola.userhandlerbe2.utils;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class LineChartMaker {

    public static void plotLineChart(List<Double> values) {
        // Create X and Y axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        // Create a LineChart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Line Chart");

        // Create a series for the data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data");

        // Add data to the series
        for (int i = 0; i < values.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, values.get(i)));
        }

        // Add the series to the chart
        lineChart.getData().add(series);

        // Create the scene and set it on the stage
        Scene scene = new Scene(lineChart, 800, 600);

        // Create the stage
        Stage stage = new Stage();
        stage.setScene(scene);

        // Show the stage
        stage.show();

        // Save the chart as an image file
        saveChartAsImage(lineChart, "line_chart.png");
    }

    private static void saveChartAsImage(LineChart<Number, Number> chart, String filename) {
        try {
            // Render the chart to an image
            Scene scene = chart.getScene();
            scene.snapshot(null);

            // Convert the image to a buffered image
            javafx.scene.image.Image fxImage = scene.snapshot(null);
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(javax.imageio.ImageIO.createImageOutputStream(fxImage));

            // Save the buffered image to a file
            File file = new File(filename);
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
