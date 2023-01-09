package com.app.controller.user;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
@Getter
public class IconsController implements Initializable {
    @FXML
    private FlowPane fpIcons;

    private Stage stage;  // сцена
    private String icon = "";

    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int i = 1; i <= 24; i++) {
            ImageView image = new ImageView(new Image("/images/usersIcons/userIcon" + i + ".png"));
            String address = "userIcon" + i;
            image.setFitWidth(150);
            image.setFitHeight(150);
            image.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    icon = address;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Error error = new Error(stage, e.getMessage());
                        error.showAndWait();
                    }
                    stage.close();
                }
            });
            FlowPane.setMargin(image, new Insets(20, 0, 0, 20));
            fpIcons.getChildren().add(image);
        }
    }

    // Отмена
    @FXML
    private void cancel() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Error error = new Error(stage, e.getMessage());
            error.showAndWait();
        }
        stage.close();
    }
}
