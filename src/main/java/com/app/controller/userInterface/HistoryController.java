package com.app.controller.userInterface;

import com.app.DTO.AccountDTO;
import com.app.DTO.CardDTO;
import com.app.DTO.OperationDTO;
import com.app.DTO.UserDTO;
import com.app.client.ClientSocket;
import com.app.controller.filters.HistoryFiltersController;
import com.app.controller.launch.AuthorizationController;
import com.app.controller.user.IconsController;
import com.app.mapper.JSON;
import com.app.messages.Choice;
import com.app.model.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.Setter;
import com.app.messages.Error;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;


@Setter
public class HistoryController implements Initializable {
    @FXML
    private Button btnExit;
    @FXML
    private ImageView imageMenuMain, imageMenuTransfers, imageMenuPayments, imageMenuHistory, imageExitIcon, imageUserIcon, imageFilterPlus, imageSearchIcon, imageAdminIcon;
    @FXML
    private HBox hbFilters, hbAddFilters;
    @FXML
    private TableView<OperationDTO> tvHistory;
    @FXML
    private TextField tfSearch;
    @FXML
    private StackPane spAdmin;

    private String minSum = "", maxSum = "", period = "За все время";

    private Stage stage;  // основная сцена


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
        imageMenuHistory.setImage(new Image("/images/menuItemsIcons/historySelect.png"));
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
        imageFilterPlus.setImage(new Image("/images/icons/plus.png"));
        imageSearchIcon.setImage(new Image("/images/icons/search.png"));
        imageAdminIcon.setImage(new Image("/images/icons/admin.png"));
    }


    // Установить менеджера
    public void setUserIcon(UserDTO user) {
        imageUserIcon.setImage(new Image("/images/usersIcons/" + user.getImage() + ".png"));
        if (user.getRole().equals("admin")) {
            spAdmin.setVisible(true);
        }
        createTableHistory();
        ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
        for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
            for (CardDTO card : account.getCards()) {
                operations.addAll(card.getOperations());
            }
        }
        tvHistory.setItems(operations);
    }

    private void createTableHistory() {
        TableColumn<OperationDTO, Date> dateTableColumn = new TableColumn<>("Дата");
        TableColumn<OperationDTO, Time> timeTableColumn = new TableColumn<>("Время");
        TableColumn<OperationDTO, String> nameTableColumn = new TableColumn<>("Название операции");
        TableColumn<OperationDTO, Double> sumTableColumn = new TableColumn<>("Сумма, руб.");
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<OperationDTO, Date>("date"));
        timeTableColumn.setCellValueFactory(new PropertyValueFactory<OperationDTO, Time>("time"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<OperationDTO, String>("name"));
        sumTableColumn.setCellValueFactory(new PropertyValueFactory<OperationDTO, Double>("sum"));
        dateTableColumn.prefWidthProperty().bind(tvHistory.widthProperty().multiply(0.2));
        timeTableColumn.prefWidthProperty().bind(tvHistory.widthProperty().multiply(0.15));
        nameTableColumn.prefWidthProperty().bind(tvHistory.widthProperty().multiply(0.5));
        sumTableColumn.prefWidthProperty().bind(tvHistory.widthProperty().multiply(0.15));
        timeTableColumn.setSortable(false);
        dateTableColumn.getStyleClass().add("table-column-center");
        timeTableColumn.getStyleClass().add("table-column-center");
        sumTableColumn.getStyleClass().add("table-column-right");
        tvHistory.getColumns().addAll(dateTableColumn, timeTableColumn, nameTableColumn, sumTableColumn);
    }


    @FXML
    private void search() {
        ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
        operations = searching(operations);
        operations = filters(operations);
        tvHistory.setItems(operations);
    }

    @FXML
    private void filtersSelection() {
        openFiltersSelectionWindow();
        hbFilters.getChildren().clear();
        if (!period.equals("За все время")) {
            hbFilters.getChildren().add(createFilter(period));
        }
        if (!minSum.equals("")) {
            hbFilters.getChildren().add(createFilter("Минимальная сумма - " + minSum + " руб."));
        }
        if (!maxSum.equals("")) {
            hbFilters.getChildren().add(createFilter("Максимальная сумма - " + maxSum + " руб."));
        }
        hbFilters.getChildren().add(hbAddFilters);
        ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
        operations = searching(operations);
        operations = filters(operations);
        tvHistory.setItems(operations);
    }

    private ObservableList<OperationDTO> searching(ObservableList<OperationDTO> operations) {
        if (tfSearch.getText().trim().isBlank()) {
            for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
                for (CardDTO card : account.getCards()) {
                    operations.addAll(card.getOperations());
                }
            }
        } else {
            String query = tfSearch.getText().trim();
            ObservableList<OperationDTO> allOperations = FXCollections.observableArrayList();
            for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
                for (CardDTO card : account.getCards()) {
                    allOperations.addAll(card.getOperations());
                }
            }
            for (OperationDTO operation : allOperations) {
                if (operation.getName().toLowerCase().contains(query.toLowerCase())) {
                    operations.add(operation);
                }
            }
        }
        return operations;
    }

    private ObservableList<OperationDTO> filters(ObservableList<OperationDTO> operations) {
        if (!period.equals("За все время")) {
            operations = applyPeriodFilter(operations);
        }
        if (!minSum.equals("")) {
            operations = applyMinSumFilter(operations);
        }
        if (!maxSum.equals("")) {
            operations = applyMaxSumFilter(operations);
        }
        return operations;
    }

    private HBox createFilter(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().setAll("label-text");
        ImageView image = new ImageView();
        image.setImage(new Image("/images/icons/close.png"));
        image.setFitHeight(25);
        image.setFitWidth(25);
        HBox hBox = new HBox(lbl, image);
        hBox.setMinHeight(35);
        hBox.setMaxHeight(35);
        HBox.setMargin(image, new Insets(0, 0, 0, 15));
        HBox.setMargin(hBox, new Insets(0, 0, 0, 20));
        hBox.getStyleClass().setAll("button");
        hBox.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                hbFilters.getChildren().remove(hBox);
                if (text.contains("Минимальная")) {
                    minSum = "";
                } else if (text.contains("Максимальная")) {
                    maxSum = "";
                } else {
                    period = "За все время";
                }
                search();
            }
        });
        return hBox;
    }

    private void openFiltersSelectionWindow() {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/filters/historyFilters.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Настройка фильтров");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        HistoryFiltersController controller = loader.<HistoryFiltersController>getController();
        controller.setStage(new_stage);
        if (!minSum.equals("")) {
            controller.setMin(minSum);
        }
        if (!maxSum.equals("")) {
            controller.setMax(maxSum);
        }
        if (!period.equals("За все время")) {
            controller.setPeriod(period);
        }
        new_stage.showAndWait();
        if (controller.isFlag()) {
            minSum = controller.getMin();
            maxSum = controller.getMax();
            period = controller.getPeriod();
        }
    }

    private ObservableList<OperationDTO> applyPeriodFilter(ObservableList<OperationDTO> allOperations) {
        ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
        switch (period) {
            case "За месяц" -> {
                for (OperationDTO operation : allOperations) {
                    Duration duration = Duration.between(LocalDateTime.of(operation.getDate().toLocalDate(), operation.getTime().toLocalTime()), LocalDateTime.now());
                    if (duration.toHours() < 24 * 30) {
                        operations.add(operation);
                    }
                }
            }
            case "За неделю" -> {
                for (OperationDTO operation : allOperations) {
                    Duration duration = Duration.between(LocalDateTime.of(operation.getDate().toLocalDate(), operation.getTime().toLocalTime()), LocalDateTime.now());
                    if (duration.toHours() < 24 * 7) {
                        operations.add(operation);
                    }
                }
            }
            case "За сутки" -> {
                for (OperationDTO operation : allOperations) {
                    Duration duration = Duration.between(LocalDateTime.of(operation.getDate().toLocalDate(), operation.getTime().toLocalTime()), LocalDateTime.now());
                    if (duration.toHours() < 24) {
                        operations.add(operation);
                    }
                }
            }
            case "За час" -> {
                for (OperationDTO operation : allOperations) {
                    Duration duration = Duration.between(LocalDateTime.of(operation.getDate().toLocalDate(), operation.getTime().toLocalTime()), LocalDateTime.now());
                    if (duration.toHours() < 1) {
                        operations.add(operation);
                    }
                }
            }
        }
        return operations;
    }

    private ObservableList<OperationDTO> applyMinSumFilter(ObservableList<OperationDTO> allOperations) {
        ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
        for (OperationDTO operation : allOperations) {
            if (operation.getSum() >= Double.parseDouble(minSum)) {
                operations.add(operation);
            }
        }
        return operations;
    }

    private ObservableList<OperationDTO> applyMaxSumFilter(ObservableList<OperationDTO> allOperations) {
        ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
        for (OperationDTO operation : allOperations) {
            if (operation.getSum() <= Double.parseDouble(maxSum)) {
                operations.add(operation);
            }
        }
        return operations;
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

    @FXML
    private void goToAdmin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/userInterface/admin.fxml"));
        try {
            stage.getScene().setRoot(loader.load());
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка отображения", e.getMessage());
            error.showAndWait();
        }
        AdminController controller = loader.<AdminController>getController();
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
