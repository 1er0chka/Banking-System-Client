package com.app.controller.card;

import com.app.DTO.CardDTO;
import com.app.correctness.Correctness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ResourceBundle;


@Setter
@Getter
public class CreateCardController implements Initializable {
    @FXML
    private TextField tfNumber, tfValidity, tfCode;
    @FXML
    private Label lblIncorrectNumber, lblIncorrectCode, lblIncorrectValidity;

    private Stage stage;  // сцена
    private CardDTO card = new CardDTO();  // новая банковская карта


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTooltips();
    }

    // Создание всплывающих подсказок
    private void createTooltips() {
        tfNumber.setTooltip(new Tooltip("Введите номер карты, состоящий из 16ти цифр."));
        tfValidity.setTooltip(new Tooltip("Введите срок действия карты в формате MM/YY,\nгде MM - номер месяца, YY - номер года (две последние цифры)."));
        tfCode.setTooltip(new Tooltip("Введите код безопасности CVV, состоящий из 3х цифр."));
    }


    // Сохранить банковскую карту
    @FXML
    private void create() {
        clearErrors();
        boolean flag = true;
        if (tfNumber.getText().trim().isBlank()) {
            tfNumber.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNumber.setText("Заполните поле");
            lblIncorrectNumber.setVisible(true);
            flag = false;
        } else if (!Correctness.isCardNumberCorrect(tfNumber.getText().trim())) {
            tfNumber.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNumber.setText("Некорректный ввод");
            lblIncorrectNumber.setVisible(true);
            flag = false;
        }
        if (tfValidity.getText().trim().isBlank()) {
            tfValidity.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectValidity.setText("Заполните поле");
            lblIncorrectValidity.setVisible(true);
            flag = false;
        } else if (!Correctness.isCardValidityCorrect(tfValidity.getText().trim())) {
            tfValidity.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectValidity.setText("Некорректный ввод");
            lblIncorrectValidity.setVisible(true);
            flag = false;
        }
        if (tfCode.getText().trim().isBlank()) {
            tfCode.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectCode.setText("Заполните поле");
            lblIncorrectCode.setVisible(true);
            flag = false;
        } else if (!Correctness.isSecurityCodeCorrect(tfCode.getText().trim())) {
            tfCode.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectCode.setText("Некорректный ввод");
            lblIncorrectCode.setVisible(true);
            flag = false;
        }
        if (flag) {
            card.setCardNumber(tfNumber.getText().trim());
            card.setValidity(tfValidity.getText().trim());
            card.setSecurityCode(Integer.parseInt(tfCode.getText().trim()));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Error error = new Error(stage, e.getMessage());
                error.showAndWait();
            }
            stage.close();
        }
    }

    // Установка обычного стиля и сокрытие сообщений об ошибках
    private void clearErrors() {
        for (TextField tf : FXCollections.observableArrayList(tfNumber, tfValidity, tfCode)) {
            tf.getParent().getStyleClass().setAll("h-box");
        }
        for (Label lbl : FXCollections.observableArrayList(lblIncorrectNumber, lblIncorrectValidity, lblIncorrectCode)) {
            lbl.setVisible(false);
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
        card.setCardNumber("#none");
        stage.close();
    }
}
