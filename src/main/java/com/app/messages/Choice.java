package com.app.messages;

import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Choice extends Message {
    private final String optionRight;  // текст правой клавиши
    private final String optionLeft;  // текст левой клавиши


    // Конструктор
    public Choice(Stage primaryStage, String header, String text, String optionRight, String optionLeft) {
        super(primaryStage, header, text);
        this.optionRight = optionRight;
        this.optionLeft = optionLeft;
        this.icon = new Image("/images/messagesIcons/choiceIcon.png");
    }


    // Вывод окна сообщения и ожидание нажатия кнопки
    @Override
    public int showAndWait() {
        // Создание сцены
        super.createStage();
        // Установка вариантов ответов
        controller.setAnswerRight(optionRight);
        controller.setAnswerLeft(optionLeft);
        // Вывод окна сообщения
        stage.showAndWait();
        // Возврат номера нажатой кнопки (1 - правая, 2 - левая)
        return controller.getPressedButton();
    }
}
