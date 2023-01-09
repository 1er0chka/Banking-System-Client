package com.app.messages;

import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Info extends Message {
    // Конструктор
    public Info(Stage primaryStage, String text) {
        super(primaryStage, "", text);
        this.icon = new Image("/images/messagesIcons/infoIcon.png");
    }

    // Конструктор с заголовком
    public Info(Stage primaryStage, String header, String text) {
        super(primaryStage, header, text);
    }
}
