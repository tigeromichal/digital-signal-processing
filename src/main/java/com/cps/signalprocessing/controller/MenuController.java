package com.cps.signalprocessing.controller;

import com.cps.signalprocessing.MainApp;
import com.cps.signalprocessing.conversion.AnalogToDigitalConverter;
import com.cps.signalprocessing.conversion.DigitalToAnalogConverter;
import com.cps.signalprocessing.conversion.ErrorCalculator;
import com.cps.signalprocessing.filtering.*;
import com.cps.signalprocessing.generating.*;
import com.cps.signalprocessing.model.Signal;
import com.cps.signalprocessing.repository.Dao;
import com.cps.signalprocessing.repository.SignalFileDao;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController extends Controller {

    private final ObservableList<Signal> signals;
    private final List<Signal> selectedSignals;
    private final DecimalFormat signalCharacteristicsDoubleFormat = new DecimalFormat("#.####");
    private final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @FXML
    private Button generateSignalButton;
    @FXML
    private Button showGraphButton;
    @FXML
    private Button showHistogramButton;
    @FXML
    private Button loadFromFileButton;
    @FXML
    private Button saveToFileButton;
    @FXML
    private Button addButton;
    @FXML
    private Button subtractButton;
    @FXML
    private Button multiplyButton;
    @FXML
    private Button divideButton;
    @FXML
    private Button convolutionButton;
    @FXML
    private Button correlationButton;
    @FXML
    private Button correlationUsingConvolutionButton;
    @FXML
    private Button calcAverageButton;
    @FXML
    private Button calcAverageMagnitudeButton;
    @FXML
    private Button sampleButton;
    @FXML
    private Button quantizeButton;
    @FXML
    private Button interpolateButton;
    @FXML
    private Button reconstructButton;
    @FXML
    private Button calcPowerButton;
    @FXML
    private Button calcVarianceButton;
    @FXML
    private Button calcEffectiveValueButton;
    @FXML
    private Button calcAllParametersButton;
    @FXML
    private Button calcMseButton;
    @FXML
    private Button calcSnrButton;
    @FXML
    private Button calcPsnrButton;
    @FXML
    private Button calcMdButton;
    @FXML
    private Button calcAllComparisonsButton;
    @FXML
    private Button removeSelectedSignalsButton;
    @FXML
    private Button applyFilterButton;
    @FXML
    private Button openDistanceSimulationButton;
    @FXML
    private Label sharedLabel1;
    @FXML
    private Label sharedLabel2;
    @FXML
    private Label averageLabel;
    @FXML
    private Label averageMagnitudeLabel;
    @FXML
    private Label powerLabel;
    @FXML
    private Label varianceLabel;
    @FXML
    private Label effectiveValueLabel;
    @FXML
    private Label mseLabel;
    @FXML
    private Label snrLabel;
    @FXML
    private Label psnrLabel;
    @FXML
    private Label mdLabel;
    @FXML
    private TextField amplitudeTextField;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField periodTextField;
    @FXML
    private TextField stepSampleNumberTextField;
    @FXML
    private TextField stepTimeNumberTextField;
    @FXML
    private TextField probabilityTextField;
    @FXML
    private TextField averageTextField;
    @FXML
    private TextField dutyCycleTextField;
    @FXML
    private TextField standardDeviationTextField;
    @FXML
    private TextField samplingFrequencyTextField;
    @FXML
    private TextField acFrequencyTextField;
    @FXML
    private TextField nbitsTextField;
    @FXML
    private TextField reconstructParamTextField;
    @FXML
    private TextField filterMParameterTextField;
    @FXML
    private TextField filterFrequencyTextField;
    @FXML
    private ListView generatorsListView;
    @FXML
    private ListView signalsListView;
    @FXML
    private ChoiceBox filterTypeChoiceBox;
    @FXML
    private ChoiceBox windowFunctionChoiceBox;
    @FXML
    private Slider histogramKSlider;


    public MenuController() {
        signals = FXCollections.observableArrayList();
        selectedSignals = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> generatorsListViewItems = FXCollections.observableArrayList("Delta", "FullWaveRectifiedSine"
                , "GaussianNoise", "HalfWaveRectifiedSine", "ImpulseNoise", "PulseWave", "SineWave"
                , "SymmetricalPulseWave", "TriangleWave", "UniformNoise", "UnitStep");
        generatorsListView.setItems(generatorsListViewItems);
        generatorsListView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> updateTextFields());
        signalsListView.setItems(signals);
        signalsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        signalsListView.setCellFactory(new Callback<ListView<Signal>, ListCell<Signal>>() {
            @Override
            public ListCell<Signal> call(ListView<Signal> p) {
                return new ListCell<Signal>() {
                    @Override
                    protected void updateItem(Signal signal, boolean bln) {
                        super.updateItem(signal, bln);
                        if (signal != null) {
                            setText(signal.getType() + signals.indexOf(signal));
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        generateSignalButton.setOnAction(event -> {
            try {
                Generator generator = getSelectedGenerator();
                if (generator == null) {
                    throw new NullPointerException("Generator should not be null");
                }
                double startTime = Double.parseDouble(startTimeTextField.getText());
                double endTime = Double.parseDouble(endTimeTextField.getText());
                double amplitude = Double.parseDouble(amplitudeTextField.getText());
                double samplingFrequency = Double.parseDouble(samplingFrequencyTextField.getText());
                addSignal(generator.generate(startTime, endTime, amplitude, samplingFrequency));
                logger.info("Signal generated");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
        showGraphButton.setOnAction(event -> {
            loadSelectedSignals();
            Stage newStage = new Stage();
            newStage.setTitle("Graph");
            newStage.setScene(MainApp.getGraphView().getScene());
            newStage.show();
            GraphController graphController = (GraphController) MainApp.getGraphView().getController();
            graphController.drawChart(selectedSignals);
        });
        showHistogramButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                Stage newStage = new Stage();
                newStage.setTitle("Histogram");
                newStage.setScene(MainApp.getGraphView().getScene());
                newStage.show();
                GraphController graphController = (GraphController) MainApp.getGraphView().getController();
                graphController.drawHistogram(selectedSignals.get(0), (int) histogramKSlider.getValue());
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        loadFromFileButton.setOnAction(event -> {
            try {
                addSignal(loadFromFile());
            } catch (Exception e) {
                logger.error("Loading graph from file aborted");
            }
        });
        saveToFileButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                try {
                    saveToFile(selectedSignals.get(0));
                } catch (Exception e) {
                    logger.error("Saving graph to file aborted");
                }
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        addButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).add(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        subtractButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).subtract(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        multiplyButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).multiply(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        divideButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).divide(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        convolutionButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).convolution(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        correlationButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).correlation(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        correlationUsingConvolutionButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                addSignal(selectedSignals.get(0).correlationUsingConvolution(selectedSignals.get(1)));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        calcAverageButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                averageLabel.setText(signalCharacteristicsDoubleFormat.format(selectedSignals.get(0).average()));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        calcAverageMagnitudeButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                averageMagnitudeLabel.setText(signalCharacteristicsDoubleFormat.format(selectedSignals.get(0).averageMagnitude()));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        calcPowerButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                powerLabel.setText(signalCharacteristicsDoubleFormat.format(selectedSignals.get(0).power()));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        calcVarianceButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                varianceLabel.setText(signalCharacteristicsDoubleFormat.format(selectedSignals.get(0).variance()));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        calcEffectiveValueButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                effectiveValueLabel.setText(signalCharacteristicsDoubleFormat.format(selectedSignals.get(0).effectiveValue()));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        sampleButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                AnalogToDigitalConverter adConverter = new AnalogToDigitalConverter();
                addSignal(adConverter.sample(selectedSignals.get(0), Double.valueOf(acFrequencyTextField.getText())));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        quantizeButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                AnalogToDigitalConverter adConverter = new AnalogToDigitalConverter();
                addSignal(adConverter.quantize(selectedSignals.get(0), Integer.valueOf(nbitsTextField.getText())));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        interpolateButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                DigitalToAnalogConverter adConverter = new DigitalToAnalogConverter();
                addSignal(adConverter.interpolate(selectedSignals.get(0), Double.valueOf(acFrequencyTextField.getText())));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        reconstructButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                DigitalToAnalogConverter adConverter = new DigitalToAnalogConverter();
                addSignal(adConverter.reconstructWithSinc(selectedSignals.get(0), Double.valueOf(acFrequencyTextField.getText()), Integer.valueOf(reconstructParamTextField.getText())));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        calcMseButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                ErrorCalculator errorCalculator = new ErrorCalculator();
                mseLabel.setText(signalCharacteristicsDoubleFormat.format(errorCalculator.calculateMse(selectedSignals.get(0), selectedSignals.get(1))));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        calcSnrButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                ErrorCalculator errorCalculator = new ErrorCalculator();
                snrLabel.setText(signalCharacteristicsDoubleFormat.format(errorCalculator.calculateSnr(selectedSignals.get(0), selectedSignals.get(1))));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        calcPsnrButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                ErrorCalculator errorCalculator = new ErrorCalculator();
                psnrLabel.setText(signalCharacteristicsDoubleFormat.format(errorCalculator.calculatePsnr(selectedSignals.get(0), selectedSignals.get(1))));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        calcMdButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                ErrorCalculator errorCalculator = new ErrorCalculator();
                mdLabel.setText(signalCharacteristicsDoubleFormat.format(errorCalculator.calculateMd(selectedSignals.get(0), selectedSignals.get(1))));
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        calcAllComparisonsButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 2) {
                calcMseButton.fire();
                calcSnrButton.fire();
                calcPsnrButton.fire();
                calcMdButton.fire();
            } else {
                logger.error("Exactly two signals should be selected");
            }
        });
        calcAllParametersButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                calcAverageButton.fire();
                calcAverageMagnitudeButton.fire();
                calcPowerButton.fire();
                calcVarianceButton.fire();
                calcEffectiveValueButton.fire();
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        removeSelectedSignalsButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() > 0) {
                for (Signal signal : selectedSignals) {
                    signals.remove(signal);
                }
                selectedSignals.clear();
            } else {
                logger.error("At least one signal should be selected");
            }
        });
        applyFilterButton.setOnAction(event -> {
            loadSelectedSignals();
            if (selectedSignals.size() == 1) {
                Filter filter = new Filter(getSelectedWindowFunction(), getSelectedFilterType()
                        , Integer.valueOf(filterMParameterTextField.getText())
                        , Double.valueOf(filterFrequencyTextField.getText()));
                addSignals(filter.apply(selectedSignals.get(0)));
            } else {
                logger.error("Exactly one signal should be selected");
            }
        });
        ObservableList<String> filterTypeChoiceBoxItems = FXCollections.observableArrayList("Low-pass", "Band-pass", "High-pass");
        filterTypeChoiceBox.setItems(filterTypeChoiceBoxItems);
        ObservableList<String> windowFunctionChoiceBoxItems = FXCollections.observableArrayList("Rectangular", "Hamming", "Hanning", "Blackman");
        windowFunctionChoiceBox.setItems(windowFunctionChoiceBoxItems);
        openDistanceSimulationButton.setOnAction(event -> {
            Stage newStage = new Stage();
            newStage.setTitle("Distance simulation");
            newStage.setScene(MainApp.getDistanceSimulationView().getScene());
            newStage.show();
        });
    }

    private void addSignals(List<Signal> signals) {
        for (Signal signal : signals) {
            addSignal(signal);
        }
    }

    private Signal loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            throw new NullPointerException("File should not be null");
        }
        Dao dao = new SignalFileDao();
        return (Signal) dao.read(file.getPath());
    }

    private void saveToFile(final Signal signal) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            throw new NullPointerException("File should not be null");
        }
        Dao dao = new SignalFileDao();
        dao.write(signal, file.getPath());
    }

    private FilterFunction getSelectedFilterType() {
        if (filterTypeChoiceBox.getSelectionModel().isEmpty()) {
            throw new NullPointerException("No filter type selected");
        }
        String name = (String) filterTypeChoiceBox.getSelectionModel().getSelectedItem();
        FilterFunction filterFunction = null;
        switch (name) {
            case "Low-pass":
                filterFunction = new LowPassFilterFunction();
                break;
            case "Band-pass":
                filterFunction = new BandPassFilterFunction();
                break;
            case "High-pass":
                filterFunction = new HighPassFilterFunction();
                break;
        }
        return filterFunction;
    }

    private WindowFunction getSelectedWindowFunction() {
        if (windowFunctionChoiceBox.getSelectionModel().isEmpty()) {
            throw new NullPointerException("No window function selected");
        }
        String name = (String) windowFunctionChoiceBox.getSelectionModel().getSelectedItem();
        WindowFunction windowFunction = null;
        switch (name) {
            case "Rectangular":
                windowFunction = new RectangularWindow();
                break;
            case "Hamming":
                windowFunction = new HammingWindow();
                break;
            case "Hanning":
                windowFunction = new HanningWindow();
                break;
            case "Blackman":
                windowFunction = new BlackmanWindow();
                break;
        }
        return windowFunction;
    }

    private Generator getSelectedGenerator() {
        if (generatorsListView.getSelectionModel().getSelectedItems().size() == 0) {
            throw new NullPointerException("No generator selected");
        }
        String name = (String) generatorsListView.getSelectionModel().getSelectedItems().get(0);
        Generator generator = null;
        switch (name) {
            case "Delta":
                generator = new Delta(Integer.valueOf(stepSampleNumberTextField.getText()));
                break;
            case "FullWaveRectifiedSine":
                generator = new FullWaveRectifiedSine(Double.valueOf(periodTextField.getText()));
                break;
            case "GaussianNoise":
                generator = new GaussianNoise(Double.valueOf(averageTextField.getText()), Double.valueOf(standardDeviationTextField.getText()));
                break;
            case "HalfWaveRectifiedSine":
                generator = new HalfWaveRectifiedSine(Double.valueOf(periodTextField.getText()));
                break;
            case "ImpulseNoise":
                generator = new ImpulsiveNoise(Double.valueOf(probabilityTextField.getText()));
                break;
            case "PulseWave":
                generator = new PulseWave(Double.valueOf(dutyCycleTextField.getText()), Double.valueOf(periodTextField.getText()));
                break;
            case "SineWave":
                generator = new SineWave(Double.valueOf(periodTextField.getText()));
                break;
            case "SymmetricalPulseWave":
                generator = new SymmetricalPulseWave(Double.valueOf(dutyCycleTextField.getText()), Double.valueOf(periodTextField.getText()));
                break;
            case "TriangleWave":
                generator = new TriangleWave(Double.valueOf(dutyCycleTextField.getText()), Double.valueOf(periodTextField.getText()));
                break;
            case "UniformNoise":
                generator = new UniformNoise();
                break;
            case "UnitStep":
                generator = new UnitStep(Double.valueOf(stepTimeNumberTextField.getText()));
                break;
        }
        return generator;
    }

    private void updateTextFields() {
        if (generatorsListView.getSelectionModel().getSelectedItems().size() == 0) {
            throw new NullPointerException("No generator chosen");
        }
        String name = (String) generatorsListView.getSelectionModel().getSelectedItems().get(0);
        switch (name) {
            case "Delta":
                periodTextField.setVisible(false);
                stepSampleNumberTextField.setVisible(true);
                stepTimeNumberTextField.setVisible(false);
                probabilityTextField.setVisible(false);
                averageTextField.setVisible(false);
                dutyCycleTextField.setVisible(false);
                standardDeviationTextField.setVisible(false);
                sharedLabel1.setText("Step sample number");
                sharedLabel2.setText("");
                break;
            case "FullWaveRectifiedSine":
            case "HalfWaveRectifiedSine":
            case "SineWave":
                periodTextField.setVisible(true);
                stepSampleNumberTextField.setVisible(false);
                stepTimeNumberTextField.setVisible(false);
                probabilityTextField.setVisible(false);
                averageTextField.setVisible(false);
                dutyCycleTextField.setVisible(false);
                standardDeviationTextField.setVisible(false);
                sharedLabel1.setText("Period");
                sharedLabel2.setText("");
                break;
            case "GaussianNoise":
                periodTextField.setVisible(false);
                stepSampleNumberTextField.setVisible(false);
                stepTimeNumberTextField.setVisible(false);
                probabilityTextField.setVisible(false);
                averageTextField.setVisible(true);
                dutyCycleTextField.setVisible(false);
                standardDeviationTextField.setVisible(true);
                sharedLabel1.setText("Average");
                sharedLabel2.setText("Standard deviation");
                break;
            case "UniformNoise":
                periodTextField.setVisible(false);
                stepSampleNumberTextField.setVisible(false);
                stepTimeNumberTextField.setVisible(false);
                probabilityTextField.setVisible(false);
                averageTextField.setVisible(false);
                dutyCycleTextField.setVisible(false);
                standardDeviationTextField.setVisible(false);
                sharedLabel1.setText("");
                sharedLabel2.setText("");
                break;
            case "ImpulseNoise":
                periodTextField.setVisible(false);
                stepSampleNumberTextField.setVisible(false);
                stepTimeNumberTextField.setVisible(false);
                probabilityTextField.setVisible(true);
                averageTextField.setVisible(false);
                dutyCycleTextField.setVisible(false);
                standardDeviationTextField.setVisible(false);
                sharedLabel1.setText("Probability");
                sharedLabel2.setText("");
                break;
            case "PulseWave":
            case "SymmetricalPulseWave":
            case "TriangleWave":
                periodTextField.setVisible(true);
                stepSampleNumberTextField.setVisible(false);
                stepTimeNumberTextField.setVisible(false);
                probabilityTextField.setVisible(false);
                averageTextField.setVisible(false);
                dutyCycleTextField.setVisible(true);
                standardDeviationTextField.setVisible(false);
                sharedLabel1.setText("Period");
                sharedLabel2.setText("Duty cycle");
                break;
            case "UnitStep":
                periodTextField.setVisible(false);
                stepSampleNumberTextField.setVisible(false);
                stepTimeNumberTextField.setVisible(true);
                probabilityTextField.setVisible(false);
                averageTextField.setVisible(false);
                dutyCycleTextField.setVisible(false);
                standardDeviationTextField.setVisible(false);
                sharedLabel1.setText("Step currentTime");
                sharedLabel2.setText("");
                break;
        }
    }

    private void loadSelectedSignals() {
        selectedSignals.clear();
        for (Object signal : signalsListView.getSelectionModel().getSelectedItems()) {
            selectedSignals.add((Signal) (signal));
        }
    }

    private void addSignal(final Signal signal) {
        signals.add(signal);
        signalsListView.getSelectionModel().clearAndSelect(signals.size() - 1);
    }

}
