package com.app.controller.account;

import com.app.DTO.AccountDTO;
import com.app.correctness.Correctness;
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
public class EditAccountController implements Initializable {
    @FXML
    private TextField tfName, tfNumber, tfAmount;
    @FXML
    private Label lblIncorrectName;

    private Stage stage;  // сцена
    private AccountDTO account = new AccountDTO();  // изменяемый банковский счет


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfName.setTooltip(new Tooltip("Введите название счета.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
    }

    // Установка изменяемого банковского счета
    public void setAccount(AccountDTO account) {
        this.account = account;
        tfName.setText(account.getAccountName());
        tfAmount.setText(account.getAmount() + "");
        tfNumber.setText(account.getAccountNumber());
    }


    // Сохранить банковский счет
    @FXML
    private void edit() {
        tfName.getParent().getStyleClass().setAll("h-box");
        lblIncorrectName.setVisible(false);
        if (tfName.getText().trim().isBlank()) {
            tfName.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectName.setText("Заполните поле");
            lblIncorrectName.setVisible(true);
        } else if (!Correctness.isRussianWordCorrect(tfName.getText().trim())) {
            tfName.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectName.setText("Некорректный ввод");
            lblIncorrectName.setVisible(true);
        } else {
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
