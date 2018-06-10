package com.cps.signalprocessing.controller;

import com.cps.signalprocessing.model.Signal;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class GraphController extends Controller {

    @FXML
    private Pane graphRoot;

    private BarChart<String, Number> barChart;
    private XYChart<Number, Number> chart;

    private Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void drawChart(final List<Signal> signals) {
        boolean isDiscrete = false;
        double minValue = 0.0;
        double maxValue = 0.0;
        for (Signal signal : signals) {
            for (double value : signal.getValues()) {
                if (value < minValue) {
                    minValue = value;
                }
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }
        double endTime = 0.0;
        for (Signal signal : signals) {
            if (signal.isDiscrete()) {
                isDiscrete = true;
            }
            if (signal.getEndTime() > endTime) {
                endTime = signal.getEndTime();
            }
        }
        final NumberAxis xAxis = new NumberAxis(0, endTime, 1);
        final NumberAxis yAxis = new NumberAxis(minValue, maxValue, 100);
        if (isDiscrete) {
            drawSignalsChart(new ScatterChart(xAxis, yAxis), signals);
        } else {
            drawSignalsChart(new LineChart(xAxis, yAxis), signals);
        }
    }

    private void drawSignalsChart(XYChart<Number, Number> chart, List<Signal> signals) {
        removeGraph();
        this.chart = chart;
        chart.setTitle("Signal");
        chart.getXAxis().setLabel("currentTime");
        chart.getYAxis().setLabel("value");

        for (int i = 0; i < signals.size(); ++i) {
            Signal signal = signals.get(i);
            XYChart.Series series = new XYChart.Series();
            series.setName("Signal" + i);
            double time = signal.getStartTime();
            double samplingPeriod = 1.0 / signal.getSamplingFrequency();
            for (double value : signal.getValues()) {
                series.getData().add(new XYChart.Data(time, value));
                time += samplingPeriod;
            }
            chart.getData().add(series);
        }

        chart.setPrefWidth(graphRoot.getPrefWidth() - 30);
        graphRoot.getChildren().add(chart);
    }

    public void drawHistogram(final Signal signal, final int k) {
        removeGraph();

        double[] values = signal.getValues();
        double minValue = values[0];
        double maxValue = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > maxValue) {
                maxValue = values[i];
            }
            if (values[i] < minValue) {
                minValue = values[i];
            }
        }
        double span = maxValue - minValue;
        double step = span / k;
        int[] group = new int[k];
        for (double value : signal.getValues()) {
            int groupIndex = (int) ((value - minValue) / step);
            if (groupIndex == k) {
                groupIndex--;
            }
            group[groupIndex]++;
        }

        int maxCount = group[0];
        for (int i = 1; i < k; i++) {
            if (group[i] > maxCount) {
                maxCount = group[i];
            }
        }

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0, maxCount, 100);
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Histogram");
        xAxis.setLabel("range");
        yAxis.setLabel("population");
        barChart.setCategoryGap(10);
        barChart.setBarGap(0);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Signal");

        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = 0; i < k; i++) {
            String xValue = df.format(i * 1.0 * step + minValue) + "-" + df.format((i + 1) * 1.0 * step + minValue);
            series1.getData().add(new XYChart.Data(xValue, group[i]));
        }
        barChart.getData().addAll(series1);
        barChart.setPrefWidth(graphRoot.getPrefWidth() - 30);
        graphRoot.getChildren().add(barChart);
    }

    private void removeGraph() {
        if (barChart != null && graphRoot.getChildren().contains(barChart)) {
            graphRoot.getChildren().remove(barChart);
        }
        if (chart != null && graphRoot.getChildren().contains(chart)) {
            graphRoot.getChildren().remove(chart);
        }
    }

}
