package com.app.controller.message;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class MessageController implements Initializable {
    @FXML
    private Label lblTitle, lblText;
    @FXML
    private ImageView imageIcon;
    @FXML
    private Button btnAnswerLeft, btnAnswerRight;

    private int pressedButton = 0;  // код нажатой клавиши


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    // Установить заголовок сообщения
    public void setHeader(String header) {
        lblTitle.setText("   " + header);
    }

    // Установить текст сообщения
    public void setText(String text) {
        lblText.setText("   " + text);
    }

    // Установить изображение
    public void setImage(Image image) {
        imageIcon.setImage(image);
    }

    // Установка текста в левую кнопку
    public void setAnswerLeft(String option) {
        btnAnswerLeft.setVisible(true);
        btnAnswerLeft.setText(option);
    }

    // Установка текста в правую кнопку
    public void setAnswerRight(String option) {
        btnAnswerRight.setVisible(true);
        btnAnswerRight.setText(option);
    }

    // Получить номер нажатой кнопки
    public int getPressedButton() {
        return pressedButton;
    }


    // Нажатие левой кнопки
    @FXML
    private void returnAnswerLeft() {
        pressedButton = 2;
        // Закрытие окна
        ((Stage) btnAnswerRight.getScene().getWindow()).close();
    }

    // Нажатие правой кнопки
    @FXML
    private void returnAnswerRight() {
        pressedButton = 1;
        // Закрытие окна
        ((Stage) btnAnswerRight.getScene().getWindow()).close();
    }
}
