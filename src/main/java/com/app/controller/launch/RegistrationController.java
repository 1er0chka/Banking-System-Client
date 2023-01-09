package com.app.controller.launch;

import com.app.DTO.UserDTO;
import com.app.client.ClientSocket;
import com.app.correctness.Correctness;
import com.app.mapper.JSON;
import com.app.model.Request;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Setter;
import com.app.messages.Choice;
import com.app.messages.Info;
import com.app.messages.Error;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


@Setter
public class RegistrationController implements Initializable {
    @FXML
    private VBox vboxRegistration, vboxRegistrationPartOne, vboxRegistrationPartTwo;
    @FXML
    private Label lblTitleRegistration, lblIncorrectSurname, lblIncorrectName, lblIncorrectSecondName, lblIncorrectDateOfBirth, lblIncorrectNewLogin, lblIncorrectNewPassword;
    @FXML
    private ImageView imageExitIcon;
    @FXML
    private TextField tfNewUserSurname, tfNewUserName, tfNewUserSecondName, tfNewUserDateOfBirth, tfNewUserLogin, pfNewUserPassword;

    private Stage stage;  // основная сцена


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
        vboxRegistration.getChildren().setAll(vboxRegistrationPartOne);
        createTooltips();
    }

    // Создание всплывающих подсказок
    private void createTooltips() {
        tfNewUserSurname.setTooltip(new Tooltip("Введите свою фамилию.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfNewUserName.setTooltip(new Tooltip("Введите свое имя.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfNewUserSecondName.setTooltip(new Tooltip("Введите свое отчество.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfNewUserDateOfBirth.setTooltip(new Tooltip("Введите свою дату рождения в формате XX.XX.XXXX."));
        tfNewUserLogin.setTooltip(new Tooltip("Придумайте логин.\nИспользуйте для ввода буквы английского алфавита, цифры, символы: \"_\", \"-\" и \".\"." + "\nЛогин должен содержать буквы разного регистра (большие и маленькие)\nи иметь длину не менее 8ми символов и не более 20ти символов."));
        pfNewUserPassword.setTooltip(new Tooltip("Придумайте пароль.\nИспользуйте для ввода буквы английского алфавита и цифры." + "\nПароль должен содержать буквы разного регистра (большие и маленькие)\nи иметь длину не менее 8ми символов и не более 20ти символов."));
    }


    // Установить сцену
    public void setStage(Stage stage) {
        this.stage = stage;
        autoClosing();
    }


    // Создание нового пользователя
    @FXML
    private void register() {
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.Registration, (new JSON<UserDTO>()).toJson(new UserDTO(tfNewUserLogin.getText().trim(), pfNewUserPassword.getText().trim(), tfNewUserSurname.getText().trim(), tfNewUserName.getText().trim(), tfNewUserSecondName.getText().trim(), LocalDate.of(Integer.parseInt(tfNewUserDateOfBirth.getText().trim().split("\\.")[2]), Integer.parseInt(tfNewUserDateOfBirth.getText().trim().split("\\.")[1]), Integer.parseInt(tfNewUserDateOfBirth.getText().trim().split("\\.")[0]))), UserDTO.class)));
            String message = ClientSocket.getInstance().get().getRequestMessage();
            switch (message) {
                case "true" -> {
                    Info info = new Info(stage, "", "Пользователь успешно зарегистрирован");
                    info.showAndWait();
                    clearNewUser();
                    goToAuthorization();
                }
                case "fio" -> {
                    Error error = new Error(stage, "Не удалось создать пользователя", "Пользователь с данными ФИО уже зарегистрирован. Проверьте введенные данные либо войдите в аккаунт.");
                    error.showAndWait();
                }
                case "login" -> {
                    Error error = new Error(stage, "Не удалось создать пользователя", "Пользователь с данным логином уже зарегистрирован. Придумайте новый логин либо войдите в аккаунт.");
                    error.showAndWait();
                }
            }
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка регистрации", e.getMessage());
            error.showAndWait();
        }
    }

    // Отмена регистрации нового пользователя
    @FXML
    private void cancelRegistration() {
        Choice exit = new Choice(stage, "Завершение процесса регистрации", "Вы уверены что хотите покинуть регистрацию? Введенные данные не сохранятся.", "Продолжить", "Покинуть");
        if (exit.showAndWait() == 2) {
            clearNewUser();
            goToAuthorization();
        }
    }

    // Установка обычного стиля и сокрытие сообщений об ошибках
    private void clearErrors() {
        pfNewUserPassword.getParent().getStyleClass().setAll("h-box");
        for (TextField tf : FXCollections.observableArrayList(tfNewUserSurname, tfNewUserName, tfNewUserSecondName, tfNewUserDateOfBirth, tfNewUserLogin)) {
            tf.getParent().getStyleClass().setAll("h-box");
        }
        for (Label lbl : FXCollections.observableArrayList(lblIncorrectSurname, lblIncorrectName, lblIncorrectSecondName, lblIncorrectDateOfBirth, lblIncorrectNewLogin, lblIncorrectNewPassword)) {
            lbl.setVisible(false);
        }
    }

    // Очистка введенных данных
    private void clearNewUser() {
        for (TextField tf : FXCollections.observableArrayList(tfNewUserSurname, tfNewUserName, tfNewUserSecondName, tfNewUserDateOfBirth, tfNewUserLogin)) {
            tf.setText("");
        }
        clearErrors();
    }

    // Проверка данных пользователя при регистрации
    @FXML
    private void registerCheck() {
        clearErrors();
        boolean flag = true;  // флаг отсутствия ошибок
        // Если поле "логин" пустое
        if (tfNewUserLogin.getText().trim().isBlank()) {
            // Установить стиль "ошибка" и показать сообщение об ошибке
            tfNewUserLogin.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNewLogin.setText("Заполните поле");
            lblIncorrectNewLogin.setVisible(true);
            flag = false;
        } else if (!Correctness.isLoginCorrect(tfNewUserLogin.getText().trim())) {
            // Установить стиль "ошибка" и показать сообщение об ошибке
            tfNewUserLogin.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNewLogin.setText("Некорректный ввод");
            lblIncorrectNewLogin.setVisible(true);
            flag = false;
        }
        // Если поле "пароль" пустое
        if (pfNewUserPassword.getText().trim().isBlank()) {
            // Установить стиль "ошибка" и показать сообщение об ошибке
            pfNewUserPassword.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNewPassword.setText("Заполните поле");
            lblIncorrectNewPassword.setVisible(true);
            flag = false;
        } else if (!Correctness.isPasswordCorrect(pfNewUserPassword.getText().trim())) {
            // Установить стиль "ошибка" и показать сообщение об ошибке
            pfNewUserPassword.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectNewPassword.setText("Некорректный ввод");
            lblIncorrectNewPassword.setVisible(true);
            flag = false;
        }
        if (flag) {
            register();
        }
    }

    // Проверка данных, введенных в поле при регистрации
    private boolean isWordCorrect(TextField tf, Label lblIncorrect) {
        // Если поле пустое
        if (tf.getText().trim().isBlank()) {
            // Установить стиль "ошибка" и показать сообщение об ошибке
            tf.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrect.setText("Заполните поле");
            lblIncorrect.setVisible(true);
            return true;
        }
        // Если данные некорректны
        if (!Correctness.isRussianWordCorrect(tf.getText().trim())) {
            // Установить стиль "ошибка" и показать сообщение об ошибке
            tf.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrect.setText("Некорректный ввод");
            lblIncorrect.setVisible(true);
            return true;
        }
        return false;
    }


    // Переход на первый экран регистрации
    @FXML
    private void goToRegistrationPartOne() {
        vboxRegistration.getChildren().setAll(lblTitleRegistration, vboxRegistrationPartOne);
    }

    // Переход на второй экран регистрации
    @FXML
    private void goToRegistrationPartTwo() {
        clearErrors();
        boolean flag = !isWordCorrect(tfNewUserSurname, lblIncorrectSurname);
        if (isWordCorrect(tfNewUserName, lblIncorrectName)) {
            flag = false;
        }
        if (isWordCorrect(tfNewUserSecondName, lblIncorrectSecondName)) {
            flag = false;
        }
        if (tfNewUserDateOfBirth.getText().trim().isBlank()) {
            tfNewUserDateOfBirth.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectDateOfBirth.setText("Заполните поле");
            lblIncorrectDateOfBirth.setVisible(true);
            flag = false;
        } else if (!Correctness.isDateCorrect(tfNewUserDateOfBirth.getText().trim())) {
            tfNewUserDateOfBirth.getParent().getStyleClass().setAll("h-box-error");
            lblIncorrectDateOfBirth.setText("Некорректный ввод");
            lblIncorrectDateOfBirth.setVisible(true);
            flag = false;
        }
        if (flag) {
            vboxRegistration.getChildren().setAll(lblTitleRegistration, vboxRegistrationPartTwo);
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
