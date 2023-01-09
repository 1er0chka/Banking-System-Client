package com.app.controller.user;

import com.app.DTO.AccountDTO;
import com.app.DTO.CardDTO;
import com.app.DTO.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ResourceBundle;


@Setter
@Getter
public class UserInfoController implements Initializable {
    @FXML
    private ImageView imageUser;
    @FXML
    private Label lblSurname, lblName, lblRole, lblAccounts, lblCards, lblOperations;

    private Stage stage;  // сцена
    private UserDTO user = new UserDTO();  // изменяемый банковский счет


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setUser(UserDTO user) {
        imageUser.setImage(new Image("/images/usersIcons/" + user.getImage() + ".png"));
        lblSurname.setText(user.getSurname());
        lblName.setText(user.getName() + " " + user.getSecondName());
        switch (user.getRole()) {
            case "admin" -> {
                lblRole.setText("Администратор");
            }
            case "user" -> {
                lblRole.setText("Пользователь");
            }
            case "block" -> {
                lblRole.setText("Заблокирован");
            }
        }
        lblAccounts.setText("Открытых счетов: " + user.getAccounts().size());
        int numberCards = 0, numberOperations = 0;
        for (AccountDTO account : user.getAccounts()) {
            numberCards += account.getCards().size();
            for (CardDTO card : account.getCards()) {
                numberOperations += card.getOperations().size();
            }
        }
        lblCards.setText("Банковских карт: " + numberCards);
        lblOperations.setText("Совершено операций: " + numberOperations);
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
