package com.app.controller.user;

import com.app.DTO.UserDTO;
import com.app.correctness.Correctness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ResourceBundle;


@Setter
@Getter
public class EditUserController implements Initializable {
    @FXML
    private TextField tfSurname, tfName, tfSecondName;
    @FXML
    private ComboBox<String> cbRole;

    private Stage stage;  // сцена
    private UserDTO user = new UserDTO();  // изменяемый банковский счет


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbRole.setItems(FXCollections.observableArrayList("admin", "user"));
    }

    // Установка изменяемого банковского счета
    public void setUser(UserDTO user) {
        this.user = user;
        tfSurname.setText(user.getSurname());
        tfName.setText(user.getName());
        tfSecondName.setText(user.getSecondName());
        cbRole.getSelectionModel().select(user.getRole());
    }


    // Сохранить банковский счет
    @FXML
    private void edit() {
        boolean flag = true;
        tfSurname.getParent().getStyleClass().setAll("h-box");
        if (tfSurname.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfSurname.getText().trim())) {
            tfSurname.getParent().getStyleClass().setAll("h-box-error");
            flag = false;
        }
        tfName.getParent().getStyleClass().setAll("h-box");
        if (tfName.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfName.getText().trim())) {
            tfName.getParent().getStyleClass().setAll("h-box-error");
            flag = false;
        }
        tfSecondName.getParent().getStyleClass().setAll("h-box");
        if (tfSecondName.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfSecondName.getText().trim())) {
            tfSecondName.getParent().getStyleClass().setAll("h-box-error");
            flag = false;
        }
        if (flag) {
            user.setSurname(tfSurname.getText().trim());
            user.setName(tfName.getText().trim());
            user.setSecondName(tfSecondName.getText().trim());
            user.setRole(cbRole.getSelectionModel().getSelectedItem());
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
        user.setSurname("none");
        stage.close();
    }
}
