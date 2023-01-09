package com.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.awt.*;


public class Main extends Application {
    public static void main(String args[]) {
        Application.launch(args);
    }

    @Override
    public void start(Stage new_stage) throws Exception {
        Stage stage = new_stage;
        Parent root = FXMLLoader.load(getClass().getResource("/com/app/launch/launch.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Universal Banking");
        stage.getIcons().add(new Image("/images/applicationIcon.png"));
        stage.setHeight((Toolkit.getDefaultToolkit().getScreenSize()).getHeight());
        stage.setWidth((Toolkit.getDefaultToolkit().getScreenSize()).getWidth());
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
    }
}
