package com.cps.signalprocessing;

import com.cps.signalprocessing.view.DistanceSimulationView;
import com.cps.signalprocessing.view.GraphView;
import com.cps.signalprocessing.view.MenuView;
import com.cps.signalprocessing.view.View;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static View menuView;
    private static View graphView;
    private static View distanceSimulationView;

    private final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(final String[] args) {
        launch(args);
    }

    public static View getMenuView() {
        return menuView;
    }

    public static View getGraphView() {
        return graphView;
    }

    public static View getDistanceSimulationView() {
        return distanceSimulationView;
    }

    public void start(final Stage primaryStage) {
        try {
            menuView = new MenuView("/views/MenuScene.fxml");
            graphView = new GraphView("/views/GraphScene.fxml");
            distanceSimulationView = new DistanceSimulationView("/views/DistanceSimulationScene.fxml");
        } catch (Exception e) {
            logger.error(e.toString());
        }

        menuView.getController().setStage(primaryStage);
        graphView.getController().setStage(primaryStage);
        distanceSimulationView.getController().setStage(primaryStage);

        primaryStage.setTitle("Digital Signal Processing");
        primaryStage.setScene(menuView.getScene());
        primaryStage.show();
    }

}
