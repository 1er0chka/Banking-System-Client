package com.app.controller.launch;

import com.app.DTO.UserDTO;
import com.app.client.ClientSocket;
import com.app.controller.userInterface.MainController;
import com.app.mapper.JSON;
import com.app.model.Request;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Setter;
import com.app.messages.Error;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


@Setter
public class AuthorizationController implements Initializable {
    @FXML
    private Label lblIncorrectLoginPassword;
    @FXML
    private ImageView imageUserIcon, imageKeyIcon, imageHidingIcon, imageExitIcon;
    @FXML
    private TextField tfUserLogin, tfUserPassword;
    @FXML
    private PasswordField pfUserPassword;

    private Stage stage;  // основная сцена


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageUserIcon.setImage(new Image("/images/icons/user.png"));
        imageKeyIcon.setImage(new Image("/images/icons/key.png"));
        imageHidingIcon.setImage(new Image("/images/icons/eye.png"));
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
    }


    // Установить сцену
    public void setStage(Stage stage) {
        this.stage = stage;
        autoClosing();
    }

    // Скрыть/показать пароль
    @FXML
    private void hidePassword() {
        if (pfUserPassword.isVisible()) {
            tfUserPassword.setText(pfUserPassword.getText());
            pfUserPassword.setVisible(false);
            tfUserPassword.setVisible(true);
        } else {
            pfUserPassword.setText(tfUserPassword.getText());
            pfUserPassword.setVisible(true);
            tfUserPassword.setVisible(false);
        }
    }

    // Проверка данных пользователя при авторизации
    @FXML
    private void loginCheck() {
        lblIncorrectLoginPassword.setVisible(false);
        if (tfUserLogin.getText().trim().isBlank()) {
            tfUserLogin.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectLoginPassword.setVisible(true);
        }
        if ((tfUserPassword.isVisible() && tfUserPassword.getText().trim().isBlank()) || (pfUserPassword.isVisible() && pfUserPassword.getText().trim().isBlank())) {
            tfUserPassword.getParent().getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectLoginPassword.setVisible(true);
        }
        if (!lblIncorrectLoginPassword.isVisible()) {
            login();
        }
    }

    // Авторизация пользователя
    private void login() {
        String password;
        if (tfUserPassword.isVisible()) {
            password = tfUserPassword.getText().trim();
        } else {
            password = pfUserPassword.getText().trim();
        }
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.LogIn, (new JSON<UserDTO>()).toJson(new UserDTO(tfUserLogin.getText().trim(), password), UserDTO.class)));
            String message = ClientSocket.getInstance().get().getRequestMessage();
            if (message.equals("false")) {
                lblIncorrectLoginPassword.setText("Неверный логин или пароль");
                lblIncorrectLoginPassword.setVisible(true);
            } else {
                ClientSocket.getInstance().setUser((new JSON<UserDTO>()).fromJson(message, UserDTO.class));
                ClientSocket.getInstance().refreshUser();
                goToUserInterface();
            }
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка авторизации", e.getMessage());
            error.showAndWait();
        }
    }


    // Переход на экран регистрации
    @FXML
    private void goToRegistration() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/launch/registration.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        RegistrationController controller = loader.<RegistrationController>getController();
        controller.setStage(stage);
        stage.setFullScreen(true);
    }

    // Переход на экран пользователя
    private void goToUserInterface() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/userInterface/main.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        MainController controller = loader.<MainController>getController();
        controller.setUserIcon(ClientSocket.getInstance().getUser());
        controller.setStage(stage);
        controller.autoClosing();
        stage.setFullScreen(true);
    }


    // Отключение от сервера при закрытии окна
    private void autoClosing() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    ClientSocket.getInstance().close();
                } catch (IOException e) {
                    Error error = new Error(stage, "Не удалось отключиться от сервера", e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    // Закрытие окна и выход из программы
    @FXML
    private void exit() {
        try {
            Thread.sleep(500);
            ClientSocket.getInstance().close();
            stage.close();
        } catch (InterruptedException e) {
            Error error = new Error(stage, e.getMessage());
            error.showAndWait();
        } catch (IOException e) {
            Error error = new Error(stage, "Не удалось отключиться от сервера", e.getMessage());
            error.showAndWait();
        }
    }
}
