package com.app.controller.launch;

import com.app.client.ClientSocket;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
public class LaunchController implements Initializable {
    @FXML
    private ImageView imageExitIcon;
    private Stage stage;  // основная сцена


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
    }

    // Установить сцену
    public void setStage(Stage stage) {
        this.stage = stage;
        autoClosing();
    }


    // Соединение с сервером
    @FXML
    private void connect() {
        try {
            autoClosing();
            if (ClientSocket.getInstance().connect()) {
                goToAuthorization();
            } else {
                Error error = new Error(stage, "Не удалось подключиться к серверу", "Сервер не отвечает по техническим причинам. Попробуйте подключиться позже");
                error.showAndWait();
            }
        } catch (Exception e) {
            Error error = new Error(stage, "Не удалось подключиться к серверу", e.getMessage());
            error.showAndWait();
        }
    }


    // Переход на экран авторизации
    private void goToAuthorization() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/launch/authorization.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        AuthorizationController controller = loader.<AuthorizationController>getController();
        controller.setStage(stage);
    }


    // Отключение от сервера при закрытии окна
    private void autoClosing() {
        stage = (Stage) imageExitIcon.getScene().getWindow();
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
        stage = (Stage) imageExitIcon.getScene().getWindow();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Error error = new Error(stage, e.getMessage());
            error.showAndWait();
        }
        stage.close();
    }
}
