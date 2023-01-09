package com.app.controller.userInterface;

import com.app.DTO.UserDTO;
import com.app.client.ClientSocket;
import com.app.controller.launch.AuthorizationController;
import com.app.controller.user.EditUserController;
import com.app.controller.user.IconsController;
import com.app.controller.user.UserInfoController;
import com.app.mapper.JSON;
import com.app.messages.Choice;
import com.app.model.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.Setter;
import com.app.messages.Error;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


@Setter
public class AdminController implements Initializable {
    @FXML
    private Button btnExit;
    @FXML
    private ImageView imageMenuMain, imageMenuTransfers, imageMenuPayments, imageMenuHistory, imageExitIcon, imageUserIcon, imageAdminIcon, imageSearchIcon;
    @FXML
    private TableView<UserDTO> tvUsers;
    @FXML
    private TextField tfSearch;

    private Stage stage;  // основная сцена
    private ContextMenu contextMenu = new ContextMenu();


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeImages();
    }

    // Инициализация изображений
    private void initializeImages() {
        imageMenuMain.setImage(new Image("/images/menuItemsIcons/main.png"));
        imageMenuTransfers.setImage(new Image("/images/menuItemsIcons/transfers.png"));
        imageMenuPayments.setImage(new Image("/images/menuItemsIcons/payments.png"));
        imageMenuHistory.setImage(new Image("/images/menuItemsIcons/history.png"));
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
        imageAdminIcon.setImage(new Image("/images/icons/adminSelect.png"));
        imageSearchIcon.setImage(new Image("/images/icons/search.png"));
    }

    public void setUserIcon(UserDTO user) {
        stage = (Stage) imageAdminIcon.getScene().getWindow();
        imageUserIcon.setImage(new Image("/images/usersIcons/" + user.getImage() + ".png"));
        createTableUsers();
        ObservableList<UserDTO> users = FXCollections.observableArrayList();
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
            users.setAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tvUsers.setItems(users);
    }

    private void createTableUsers() {
        TableColumn<UserDTO, Integer> idTableColumn = new TableColumn<>("id");
        TableColumn<UserDTO, String> loginTableColumn = new TableColumn<>("Логин");
        TableColumn<UserDTO, String> roleTableColumn = new TableColumn<>("Статус");
        TableColumn<UserDTO, String> surnameTableColumn = new TableColumn<>("Фамилия");
        TableColumn<UserDTO, String> nameTableColumn = new TableColumn<>("Имя");
        TableColumn<UserDTO, String> secondNameTableColumn = new TableColumn<>("Отчество");
        idTableColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, Integer>("id"));
        loginTableColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("login"));
        roleTableColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("role"));
        surnameTableColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("surname"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("name"));
        secondNameTableColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("secondName"));
        idTableColumn.prefWidthProperty().bind(tvUsers.widthProperty().multiply(0.05));
        loginTableColumn.prefWidthProperty().bind(tvUsers.widthProperty().multiply(0.25));
        roleTableColumn.prefWidthProperty().bind(tvUsers.widthProperty().multiply(0.1));
        surnameTableColumn.prefWidthProperty().bind(tvUsers.widthProperty().multiply(0.2));
        nameTableColumn.prefWidthProperty().bind(tvUsers.widthProperty().multiply(0.2));
        secondNameTableColumn.prefWidthProperty().bind(tvUsers.widthProperty().multiply(0.2));
        idTableColumn.getStyleClass().add("table-column-right");
        loginTableColumn.getStyleClass().add("table-column-right");
        roleTableColumn.getStyleClass().add("table-column-center");
        surnameTableColumn.getStyleClass().add("table-column-right");
        nameTableColumn.getStyleClass().add("table-column-right");
        secondNameTableColumn.getStyleClass().add("table-column-right");
        tvUsers.getColumns().addAll(idTableColumn, loginTableColumn, roleTableColumn, surnameTableColumn, nameTableColumn, secondNameTableColumn);
        addActionToTable();
    }

    private void addActionToTable() {
        tvUsers.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contextMenu.hide();
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (tvUsers.getSelectionModel().getSelectedItem() != null) {
                        contextMenu = new ContextMenu(addShowToContextMenu(tvUsers.getSelectionModel().getSelectedItem()), addEditToContextMenu(tvUsers.getSelectionModel().getSelectedItem()), addBlockToContextMenu(tvUsers.getSelectionModel().getSelectedItem()));
                        contextMenu.show(tvUsers, event.getScreenX(), event.getScreenY());
                        tvUsers.getSelectionModel().select(null);
                    }
                }
            }
        });
    }

    private MenuItem addShowToContextMenu(UserDTO user) {
        MenuItem menuItem = new MenuItem("Просмотреть");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                openShowUser(user);
            }
        });
        return menuItem;
    }

    private MenuItem addEditToContextMenu(UserDTO user) {
        MenuItem menuItem = new MenuItem("Редактировать");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                openEditUser(user);
                ObservableList<UserDTO> users = FXCollections.observableArrayList();
                try {
                    ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
                    users.setAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tvUsers.getItems().setAll(users);
            }
        });
        return menuItem;
    }

    private MenuItem addBlockToContextMenu(UserDTO user) {
        MenuItem menuItem = new MenuItem("Заблокировать");
        if (user.getRole().equals("block")) {
            menuItem.setText("Разблокировать");
        }
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (user.getRole().equals("user")) {
                    Choice choice = new Choice(stage, "Блокировка пользователя", "Вы уверены, что хотите заблокировать клиента банка?", "Заблокировать", "Отмена");
                    if (choice.showAndWait() == 1) {
                        user.setRole("block");
                        try {
                            ClientSocket.getInstance().send(new Request(Request.RequestType.UpdateUser, (new JSON<UserDTO>()).toJson(user, UserDTO.class)));
                            ClientSocket.getInstance().refreshUser();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ObservableList<UserDTO> users = FXCollections.observableArrayList();
                        try {
                            ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
                            users.setAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        tvUsers.getItems().setAll(users);
                    }
                } else if (user.getRole().equals("block")) {
                    Choice choice = new Choice(stage, "Разблокировка пользователя", "Вы уверены, что хотите разблокировать клиента банка?", "Разблокировать", "Отмена");
                    if (choice.showAndWait() == 1) {
                        user.setRole("user");
                        try {
                            ClientSocket.getInstance().send(new Request(Request.RequestType.UpdateUser, (new JSON<UserDTO>()).toJson(user, UserDTO.class)));
                            ClientSocket.getInstance().refreshUser();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ObservableList<UserDTO> users = FXCollections.observableArrayList();
                        try {
                            ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
                            users.setAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        tvUsers.getItems().setAll(users);
                    }
                } else {
                    Error error = new Error(stage, "Не удалось заблокировать пользователя", "Невозможно заблокировать администратора. Смените роль в меню редактирования");
                    error.showAndWait();
                }
            }
        });
        return menuItem;
    }

    private void openEditUser(UserDTO user) {
        ObservableList<UserDTO> users = FXCollections.observableArrayList();
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
            users.addAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (UserDTO tempUser : users) {
            if (user.getId() == tempUser.getId()) {
                user = tempUser;
            }
        }
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/user/editUser.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Информация о пользователе");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        EditUserController controller = loader.<EditUserController>getController();
        controller.setStage(new_stage);
        controller.setUser(user);
        new_stage.showAndWait();
        if (controller.getUser().getSurname().equals("none")) {
            return;
        }
        user = controller.getUser();
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.UpdateUser, (new JSON<UserDTO>()).toJson(user, UserDTO.class)));
            ClientSocket.getInstance().refreshUser();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openShowUser(UserDTO user) {
        ObservableList<UserDTO> users = FXCollections.observableArrayList();
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
            users.addAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (UserDTO tempUser : users) {
            if (user.getId() == tempUser.getId()) {
                user = tempUser;
            }
        }
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/user/userInfo.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Информация о пользователе");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        UserInfoController controller = loader.<UserInfoController>getController();
        controller.setStage(new_stage);
        controller.setUser(user);
        new_stage.showAndWait();
    }


    @FXML
    private void search() {
        ObservableList<UserDTO> users = FXCollections.observableArrayList();
        users = searching(users);
        tvUsers.setItems(users);
    }

    private ObservableList<UserDTO> searching(ObservableList<UserDTO> users) {
        if (tfSearch.getText().trim().isBlank()) {
            try {
                ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
                users.setAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String query = tfSearch.getText().trim();
            ObservableList<UserDTO> allUsers = FXCollections.observableArrayList();
            try {
                ClientSocket.getInstance().send(new Request(Request.RequestType.GetAllUser, ""));
                allUsers.setAll((new JSON<UserDTO>()).fromJsonArray(ClientSocket.getInstance().get().getRequestMessage(), UserDTO.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (UserDTO user : allUsers) {
                if (user.getSurname().toLowerCase().contains(query.toLowerCase()) || user.getName().toLowerCase().contains(query.toLowerCase()) || user.getSecondName().toLowerCase().contains(query.toLowerCase()) || user.getLogin().toLowerCase().contains(query.toLowerCase())) {
                    users.add(user);
                }
            }
        }
        return users;
    }


    // Открытие окна "Главная"
    @FXML
    private void goToMain() {
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

    // Открытие окна "Переводы"
    @FXML
    private void goToTransfers() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/userInterface/transfers.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        TransfersController controller = loader.<TransfersController>getController();
        controller.setUserIcon(ClientSocket.getInstance().getUser());
        controller.setStage(stage);
        controller.autoClosing();
        stage.setFullScreen(true);
    }

    // Открытие окна "Платежи"
    @FXML
    private void goToPayments() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/userInterface/payments.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        PaymentsController controller = loader.<PaymentsController>getController();
        controller.setUserIcon(ClientSocket.getInstance().getUser());
        controller.setStage(stage);
        controller.autoClosing();
        stage.setFullScreen(true);
    }

    // Открытие окна "История"
    @FXML
    private void goToHistory() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/userInterface/history.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        HistoryController controller = loader.<HistoryController>getController();
        controller.setUserIcon(ClientSocket.getInstance().getUser());
        controller.setStage(stage);
        controller.autoClosing();
        stage.setFullScreen(true);
    }


    // Смена сцены на Авторизация
    private void goToAuthorization() {
        try {
            // Установка сцены
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/launch/authorization.fxml"));
            stage.getScene().setRoot(loader.load());
            AuthorizationController controller = loader.<AuthorizationController>getController();  // контроллер сцены
            controller.setStage(stage);
            stage.setFullScreen(true);
        } catch (Exception e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
    }


    // Открытие меню пользователя
    @FXML
    private void openUserMenu() {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/user/icons.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Выбор аватара");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        IconsController controller = loader.<IconsController>getController();
        controller.setStage(new_stage);
        new_stage.showAndWait();
        if (!controller.getIcon().equals("")) {
            try {
                ClientSocket.getInstance().getUser().setImage(controller.getIcon());
                ClientSocket.getInstance().send(new Request(Request.RequestType.UpdateUser, (new JSON<UserDTO>()).toJson(ClientSocket.getInstance().getUser(), UserDTO.class)));
                ClientSocket.getInstance().refreshUser();
                imageUserIcon.setImage(new Image("/images/usersIcons/" + ClientSocket.getInstance().getUser().getImage() + ".png"));
            } catch (IOException e) {
                Error error = new Error(stage, "Ошибка смены изображения", e.getMessage());
                error.showAndWait();
            }
        }
    }


    // Отключение от сервера при закрытии окна
    public void autoClosing() {
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
        // Смена стиля кнопки
        btnExit.getStyleClass().setAll("active-menu-button");
        imageExitIcon.setImage(new Image("/images/icons/logoutSelect.png"));
        // Вызов диалогового окна
        Choice exit = new Choice(stage, "Выход из аккаунта", "Вы уверены что хотите выйти?", "Выход", "Отмена");
        if (exit.showAndWait() == 1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Error error = new Error(stage, e.getMessage());
                error.showAndWait();
            }
            // Выход из аккаунта
            try {
                ClientSocket.getInstance().send(new Request(Request.RequestType.LogOut, ""));
                ClientSocket.getInstance().setUser(null);
            } catch (IOException e) {
                Error error = new Error(stage, "Ошибка при выходе из аккаунта", e.getMessage());
                error.showAndWait();
            }
            goToAuthorization();
        }
        // Смена стиля кнопки
        btnExit.getStyleClass().setAll("menu-button");
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
    }
}
