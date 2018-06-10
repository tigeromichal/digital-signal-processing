package com.cps.signalprocessing.view;

import com.cps.signalprocessing.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public abstract class View {

    protected Scene scene;
    protected Controller controller;

    public View(final String loaderSourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(MenuView.class.getResource(loaderSourcePath));
        Parent graphRoot = loader.load();
        scene = new Scene(graphRoot);
        controller = loader.getController();
    }

    public Scene getScene() {
        return scene;
    }

    public Controller getController() {
        return controller;
    }

}
