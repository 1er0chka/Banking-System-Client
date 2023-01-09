package com.app.controller.account;

import com.app.DTO.AccountDTO;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


@Setter
@Getter
public class AccountSelectionController implements Initializable {
    @FXML
    private FlowPane fpAccounts;

    private Stage stage;  // сцена
    private AccountDTO account = new AccountDTO();  // выбранный банковский счет


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        account.setAccountNumber("#none");
    }


    // Установка банковских счетов
    public void setAccounts(ArrayList<AccountDTO> accounts) {
        fpAccounts.getChildren().clear();
        for (AccountDTO accountDTO : accounts) {
            fpAccounts.getChildren().add(createAccountVb(accountDTO));
        }
        fpAccounts.getChildren().add(createAccountVb(new AccountDTO(0, "Пользовательский", "Введите номер", 0, null, null, null)));
    }

    // Создание vbox банковского счета
    private VBox createAccountVb(AccountDTO accountDTO) {
        // Создание текстового поля с названием банковского счета
        Label name = new Label(accountDTO.getAccountName());
        name.getStyleClass().setAll("label-text");
        VBox.setMargin(name, new Insets(20, 0, 0, 20));
        // Создание текстового поля с номером банковского счета
        Label number = new Label(accountDTO.getAccountNumber());
        number.getStyleClass().setAll("label-text");
        VBox.setMargin(number, new Insets(10, 0, 0, 20));
        // Создание vbox
        VBox vbAccount = new VBox(name, number);
        vbAccount.getStyleClass().setAll("v-box");
        VBox.setMargin(vbAccount, new Insets(20, 0, 0, 20));
        vbAccount.setMinHeight(115);
        vbAccount.setMaxHeight(115);
        vbAccount.setMinWidth(358);
        vbAccount.setMaxWidth(358);
        // Добавить действие при нажатии
        vbAccount.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Установка стилей
                for (int i = 0; i < fpAccounts.getChildren().size(); i++) {
                    fpAccounts.getChildren().get(i).getStyleClass().setAll("v-box");
                }
                vbAccount.getStyleClass().setAll("v-box-active");
                account = accountDTO;
            }
        });
        return vbAccount;
    }


    // Подтверждение выбора банковского счета
    @FXML
    private void choose() {
        if (account.getAccountNumber().equals("Введите номер")) {
            account.setAccountNumber("#empty");
        }
        if (account.getAccountNumber().equals("#none")) {
            Error error = new Error(stage, "Не выбран счет", "Выберите банковский счет для операции");
            error.showAndWait();
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Error error = new Error(stage, e.getMessage());
            error.showAndWait();
        }
        stage.close();
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
        account.setAccountNumber("#none");
        stage.close();
    }
}
