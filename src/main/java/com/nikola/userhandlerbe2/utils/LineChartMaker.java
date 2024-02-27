package com.nikola.userhandlerbe2.utils;

import com.googlecode.charts4j.*;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class LineChartMaker {

    public  String plotLineChart(List<Double> values) {
        // Create a new line
        Line line = Plots.newLine(DataUtil.scale(values), Color.BLUE);

        line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));// Create a new line chart
        LineChart chart = GCharts.newLineChart(line);

        // Set chart size
        chart.setSize(660, 440);

        // Set the title and axis labels
        chart.setTitle("Line Chart");
        chart.setGrid(1, 1000, 3, 2);
        chart.addXAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, values.size()));
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, values.stream().max(Double::compareTo).orElse(1.0)));
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 50);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        //Save the chart as an image
        return chart.toURLString();
    }
}