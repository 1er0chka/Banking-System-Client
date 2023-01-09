package com.app.messages;

import com.app.controller.message.MessageController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public abstract class Message {
    protected Stage stage;  // сцена сообщения
    protected MessageController controller;  // контроллер
    protected Stage primaryStage;  // главная сцена
    protected String header;  // заголовок сообщения
    protected String text;  // текст сообщения
    protected Image icon;  // изображение сообщения


    // Конструктор
    public Message(Stage primaryStage, String header, String text) {
        this.primaryStage = primaryStage;
        this.header = header;
        this.text = text;
    }


    // Вывод окна сообщения и ожидание нажатия кнопки
    public int showAndWait() {
        createStage();
        // Вывод окна
        stage.showAndWait();
        // Возврат номера нажатой кнопки (1 - правая, 2 - левая)
        return controller.getPressedButton();
    }

    // Создание сцены
    protected void createStage() {
        stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/message/message.fxml"));
            stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        // Установка заголовка окна
        stage.setTitle("Сообщение");
        // Блокировка всех окон кроме данного
        stage.initModality(Modality.APPLICATION_MODAL);
        // Отображение данного окна поверх главного
        stage.initOwner(primaryStage.getScene().getWindow());
        // Передача данных в контроллер
        controller = loader.<MessageController>getController();
        controller.setHeader(header);
        controller.setText(text);
        controller.setAnswerRight("Ок");
        controller.setImage(icon);
    }
}
