package com.cps.signalprocessing.controller;

import com.cps.signalprocessing.generating.SineWave;
import com.cps.signalprocessing.model.RealSignal;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistanceSimulationController extends Controller {

    private double simulatorTimeUnit;
    private double currentTime = 0.0;
    private double lastSoundingTime = 0.0;
    private double objectDistance = 0.5;
    private double objectVelocity;
    private double signalPropagationVelocity;

    private SineWave sineWave1;
    private SineWave sineWave2;
    private double samplingPeriod;
    private int bufferSize;
    private double soundingPeriod;

    private boolean simulationRunning;

    @FXML
    private Pane graph1Root;
    @FXML
    private Pane graph2Root;
    @FXML
    private Pane graph3Root;
    @FXML
    private TextField simulatorTimeUnitTextField;
    @FXML
    private TextField objectVelocityTextField;
    @FXML
    private TextField signalPropagationVelocityTextField;
    @FXML
    private TextField soundingSignalPeriodTextField;
    @FXML
    private TextField samplingFrequencyTextField;
    @FXML
    private TextField bufferSizeTextField;
    @FXML
    private TextField soundingPeriodTextField;
    @FXML
    private Label realDistanceLabel;
    @FXML
    private Label estimatedDistanceLabel;
    @FXML
    private Button startSimulationButton;
    @FXML
    private Button stopSimulationButton;

    private XYChart<Number, Number> chart1;
    private XYChart<Number, Number> chart2;
    private XYChart<Number, Number> chart3;
    private XYChart.Series<Number, Number> series1;
    private XYChart.Series<Number, Number> series2;
    private XYChart.Series<Number, Number> series3;
    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<>();
    private List<Number> signalValues = new ArrayList();

    private AnimationTimer animationTimer;
    private UpdateSimulation updateSimulation;
    private ExecutorService executor;

    private Logger logger = LoggerFactory.getLogger(DistanceSimulationController.class);

    public DistanceSimulationController() {
        executor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startSimulationButton.setOnAction(event -> {
            resetState();
            removeGraph();
            loadInput();

            final NumberAxis xAxis = new NumberAxis(0, bufferSize * samplingPeriod, 0.1);
            final NumberAxis yAxis = new NumberAxis(-2, 2, 10);
            chart1 = new LineChart(xAxis, yAxis) {
                @Override
                protected void dataItemAdded(Series series, int itemIndex, Data item) {
                }
            };
            chart2 = new LineChart(xAxis, yAxis) {
                @Override
                protected void dataItemAdded(Series series, int itemIndex, Data item) {
                }
            };
            chart3 = new LineChart(new NumberAxis(0, 2 * bufferSize * samplingPeriod, 0.1), new NumberAxis(-100, 100, 10)) {
                @Override
                protected void dataItemAdded(Series series, int itemIndex, Data item) {
                }
            };
            chart1.setAnimated(false);
            chart2.setAnimated(false);
            chart3.setAnimated(false);
            series1 = new XYChart.Series<>();
            series2 = new XYChart.Series<>();
            series3 = new XYChart.Series<>();
            series1.setName("Signal sent");
            series2.setName("Signal received");
            series3.setName("Correlation");
            chart1.getData().add(series1);
            chart2.getData().add(series2);
            chart3.getData().add(series3);
            for (int i = 0; i < bufferSize; i++) {
                series1.getData().add(new XYChart.Data<>(samplingPeriod * i, 0.0));
            }
            for (int i = 0; i < bufferSize; i++) {
                series2.getData().add(new XYChart.Data<>(samplingPeriod * i, 0.0));
            }
            for (int i = 0; i < 2 * bufferSize; i++) {
                series3.getData().add(new XYChart.Data<>(samplingPeriod * i, 0.0));
            }
            chart1.setPrefWidth(graph1Root.getPrefWidth() - 10);
            chart2.setPrefWidth(graph1Root.getPrefWidth() - 10);
            chart3.setPrefWidth(graph1Root.getPrefWidth() - 10);
            chart1.setPrefHeight(graph1Root.getPrefHeight() - 10);
            chart2.setPrefHeight(graph1Root.getPrefHeight() - 10);
            chart3.setPrefHeight(graph1Root.getPrefHeight() - 10);
            graph1Root.getChildren().add(chart1);
            graph2Root.getChildren().add(chart2);
            graph3Root.getChildren().add(chart3);

            simulationRunning = true;
            updateSimulation = new UpdateSimulation();
            executor.execute(updateSimulation);

            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    addDataToSeries();
                }
            };
            animationTimer.start();

        });
        stopSimulationButton.setOnAction(event -> {
            simulationRunning = false;
            animationTimer.stop();
        });
    }

    private void addDataToSeries() {
        ObservableList<XYChart.Data<Number, Number>> data1 = series1.getData();
        ObservableList<XYChart.Data<Number, Number>> data2 = series2.getData();
        while (!dataQ1.isEmpty()) {
            for (int i = 0; i < data1.size() - 1; i++) {
                data1.set(i, new XYChart.Data(i * samplingPeriod, data1.get(i + 1).getYValue()));
            }
            data1.set(data1.size() - 1, new XYChart.Data<>((data1.size() - 1) * samplingPeriod, dataQ1.remove()));
            int lastReceivedSampleIndex = (int) ((currentTime - 2 * objectDistance / signalPropagationVelocity) / samplingPeriod);
            if (lastReceivedSampleIndex >= 0) {
                for (int i = 0; i < data2.size() - 1; i++) {
                    data2.set(i, new XYChart.Data(i * samplingPeriod, data2.get(i + 1).getYValue()));
                }
                data2.set(data2.size() - 1, new XYChart.Data<>((data2.size() - 1) * samplingPeriod, signalValues.get(lastReceivedSampleIndex)));
                if (currentTime - lastSoundingTime >= soundingPeriod) {
                    lastSoundingTime = currentTime;
                    double[] values1 = new double[data1.size()];
                    double[] values2 = new double[data2.size()];
                    for (int i = 0; i < values1.length; i++) {
                        values1[i] = data1.get(i).getYValue().doubleValue();
                    }
                    for (int i = 0; i < values2.length; i++) {
                        values2[i] = data2.get(i).getYValue().doubleValue();
                    }
                    double[] values = new RealSignal(values1, 0.0, 1.0 / samplingPeriod)
                            .correlation(new RealSignal(values2, 0.0, 1.0 / samplingPeriod))
                            .getValues();
                    int maxValueIndex = values.length / 2;
                    for (int i = values.length / 2 + 1; i < values.length; i++) {
                        if (values[i] > values[maxValueIndex]) {
                            maxValueIndex = i;
                        }
                    }
                    double deltaTime = (maxValueIndex - (values.length / 2)) * samplingPeriod;
                    double estimatedDistance = (signalPropagationVelocity * deltaTime) / 2.0;
                    estimatedDistanceLabel.setText(String.valueOf(estimatedDistance));
                    realDistanceLabel.setText(String.valueOf(objectDistance));
                    logger.info(String.valueOf(estimatedDistance) + " " + String.valueOf(objectDistance));
                    ObservableList<XYChart.Data<Number, Number>> data3 = FXCollections.observableArrayList();
                    for (int i = 0; i < values.length; i++) {
                        data3.add(new XYChart.Data<>(i * samplingPeriod, values[i]));
                    }
                    series3.setData(data3);
                }
            }
        }
    }

    private void removeGraph() {
        if (graph1Root != null && graph1Root.getChildren().contains(chart1)) {
            graph1Root.getChildren().remove(chart1);
        }
        if (graph2Root != null && graph2Root.getChildren().contains(chart2)) {
            graph2Root.getChildren().remove(chart2);
        }
        if (graph3Root != null && graph3Root.getChildren().contains(chart3)) {
            graph3Root.getChildren().remove(chart3);
        }
    }

    private double getNextValue() {
        double value = sineWave1.getValue(0, currentTime, 1.0);
        value *= sineWave2.getValue(0, currentTime, 1.0);
        return value;
    }

    private void resetState() {
        currentTime = 0.0;
        lastSoundingTime = 0.0;
        objectDistance = 0.5;
        signalValues.removeAll(signalValues);
        dataQ1.removeAll(dataQ1);
    }

    private void loadInput() {
        simulatorTimeUnit = Double.valueOf(simulatorTimeUnitTextField.getText());
        objectVelocity = Double.valueOf(objectVelocityTextField.getText());
        signalPropagationVelocity = Double.valueOf(signalPropagationVelocityTextField.getText());
        double soundingSignalPeriod = Double.valueOf(soundingSignalPeriodTextField.getText());
        samplingPeriod = 1.0 / Double.valueOf(samplingFrequencyTextField.getText());
        bufferSize = Integer.valueOf(bufferSizeTextField.getText());
        soundingPeriod = Double.valueOf(soundingPeriodTextField.getText());

        sineWave1 = new SineWave(soundingSignalPeriod);
        sineWave2 = new SineWave(soundingSignalPeriod / 10.0);
    }

    private class UpdateSimulation implements Runnable {
        public void run() {
            try {
                double sendingValue = getNextValue();
                signalValues.add(sendingValue);
                dataQ1.add(sendingValue);
                currentTime += samplingPeriod;
                Thread.sleep((long) (simulatorTimeUnit * 1000.0));
                objectDistance += objectVelocity * samplingPeriod;
                if (simulationRunning) {
                    executor.execute(this);
                }
            } catch (InterruptedException ex) {
                logger.error("Error updating simulation", ex);
            }
        }
    }

}
