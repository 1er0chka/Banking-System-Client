package com.app.controller.account;

import com.app.DTO.AccountDTO;
import com.app.correctness.Correctness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import com.app.messages.Error;

import java.net.URL;
import java.util.ResourceBundle;


public class CreateAccountController implements Initializable {
    @FXML
    private TextField tfName, tfNumber, tfAmount;
    @FXML
    private Label lblIncorrectName, lblIncorrectNumber, lblIncorrectAmount;

    private Stage stage;  // сцена
    private AccountDTO account = new AccountDTO();  // новый банковский счет


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTooltips();
    }

    // Создание всплывающих подсказок
    private void createTooltips() {
        tfName.setTooltip(new Tooltip("Введите название счета.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfNumber.setTooltip(new Tooltip("Введите номер счета в формате LLNN LLLL NNNN NNNN NNNN NNNN NNNN без пробелов,\nгде L - большие буквы английского алфавита, а N - цифры."));
        tfAmount.setTooltip(new Tooltip("Введите сумму на счету.\nИспользуйте для ввода цифры и символ \".\"."));
    }


    // Установить сцену
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Получить банковский счет
    public AccountDTO getAccount() {
        return account;
    }


    // Создать банковский счет
    @FXML
    private void create() {
        clearErrors();
        boolean flag = true;
        if (tfName.getText().trim().isBlank()) {
            tfName.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectName.setText("Заполните поле");
            lblIncorrectName.setVisible(true);
            flag = false;
        } else if (!Correctness.isRussianWordCorrect(tfName.getText().trim())) {
            tfName.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectName.setText("Некорректный ввод");
            lblIncorrectName.setVisible(true);
            flag = false;
        }
        if (tfNumber.getText().trim().isBlank()) {
            tfNumber.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNumber.setText("Заполните поле");
            lblIncorrectNumber.setVisible(true);
            flag = false;
        } else if (!Correctness.isBankAccountNumberCorrect(tfNumber.getText().trim())) {
            tfNumber.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNumber.setText("Некорректный ввод");
            lblIncorrectNumber.setVisible(true);
            flag = false;
        }
        if (tfAmount.getText().trim().isBlank()) {
            tfAmount.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectAmount.setText("Заполните поле");
            lblIncorrectAmount.setVisible(true);
            flag = false;
        } else if (!Correctness.isSumCorrect(tfAmount.getText().trim())) {
            tfAmount.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectAmount.setText("Некорректный ввод");
            lblIncorrectAmount.setVisible(true);
            flag = false;
        }
        if (flag) {
            account.setAccountName(tfName.getText().trim());
            account.setAccountNumber(tfNumber.getText().trim());
            account.setAmount(Double.parseDouble(tfAmount.getText().trim()));
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
        for (TextField tf : FXCollections.observableArrayList(tfName, tfNumber, tfAmount)) {
            tf.getParent().getStyleClass().setAll("h-box");
        }
        for (Label lbl : FXCollections.observableArrayList(lblIncorrectName, lblIncorrectNumber, lblIncorrectAmount)) {
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
        account.setAccountName("#none");
        stage.close();
    }
}
