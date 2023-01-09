package com.app.controller.userInterface;

import com.app.DTO.AccountDTO;
import com.app.DTO.CardDTO;
import com.app.DTO.OperationDTO;
import com.app.DTO.UserDTO;
import com.app.client.ClientSocket;
import com.app.controller.account.CreateAccountController;
import com.app.controller.account.EditAccountController;
import com.app.controller.card.CreateCardController;
import com.app.controller.launch.AuthorizationController;
import com.app.controller.user.IconsController;
import com.app.mapper.JSON;
import com.app.messages.Choice;
import com.app.messages.Info;
import com.app.model.Request;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.Setter;
import com.app.messages.Error;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;


@Setter
public class MainController implements Initializable {
    @FXML
    private Button btnExit;
    @FXML
    private ImageView imageMenuMain, imageMenuTransfers, imageMenuPayments, imageMenuHistory, imageExitIcon, imageUserIcon, imageAdminIcon;
    @FXML
    private VBox vbAccounts, vbCards, vbHistory, vbOperations;
    @FXML
    private StackPane spAdmin;

    private ContextMenu contextMenu = new ContextMenu();
    private Stage stage;  // основная сцена


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contextMenu.getStyleClass().setAll("context-menu");
        initializeImages();
    }

    // Инициализация изображений
    private void initializeImages() {
        imageMenuMain.setImage(new Image("/images/menuItemsIcons/mainSelect.png"));
        imageMenuTransfers.setImage(new Image("/images/menuItemsIcons/transfers.png"));
        imageMenuPayments.setImage(new Image("/images/menuItemsIcons/payments.png"));
        imageMenuHistory.setImage(new Image("/images/menuItemsIcons/history.png"));
        imageAdminIcon.setImage(new Image("/images/icons/admin.png"));
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
    }


    // Установить менеджера
    public void setUserIcon(UserDTO user) {
        imageUserIcon.setImage(new Image("/images/usersIcons/" + user.getImage() + ".png"));
        if (user.getRole().equals("admin")) {
            spAdmin.setVisible(true);
        }
    }

    // Установить сцену
    public void setStage(Stage stage) {
        this.stage = stage;
        createAccounts();
    }


    // Создание банковских счетов
    private void createAccounts() {
        vbAccounts.getChildren().clear();
        vbHistory.setVisible(false);
        // Добавление счетов пользователя на экран
        if (ClientSocket.getInstance().getUser().getAccounts() != null) {
            for (AccountDTO accountDTO : ClientSocket.getInstance().getUser().getAccounts()) {
                vbAccounts.getChildren().add(createAccountVb(accountDTO));
            }
        }
        // Создание заголовка
        Label lblMyAccounts = new Label("Мои счета");
        VBox.setMargin(lblMyAccounts, new Insets(0, 0, 0, 10));
        // Заполнение vbox дополнительными элементами
        if (ClientSocket.getInstance().getUser().getAccounts() != null && ClientSocket.getInstance().getUser().getAccounts().size() < 6) {
            vbAccounts.getChildren().add(createAddAccountVb());
        }
        vbAccounts.getChildren().add(0, lblMyAccounts);
        // Открытие первого банковского счета
        if (ClientSocket.getInstance().getUser().getAccounts() != null && ClientSocket.getInstance().getUser().getAccounts().size() > 0) {
            vbAccounts.getChildren().get(1).getStyleClass().setAll("v-box-active");
            createCards(ClientSocket.getInstance().getUser().getAccounts().get(0));
        }
    }

    // Создание vbox банковского счета
    private VBox createAccountVb(AccountDTO accountDTO) {
        // Создание текстового поля с названием счета
        Label name = new Label(accountDTO.getAccountName());
        name.getStyleClass().setAll("label-text");
        VBox.setMargin(name, new Insets(5, 0, 0, 10));
        // Создание текстового поля с номером счета
        Label number = new Label(accountDTO.getAccountNumber());
        number.getStyleClass().setAll("label-text");
        VBox.setMargin(number, new Insets(5, 0, 0, 10));
        // Создание текстового поля с суммой на счету
        Label sum = new Label((new DecimalFormat("#.##")).format(accountDTO.getAmount()) + " руб.");
        sum.getStyleClass().setAll("label-text");
        VBox.setMargin(sum, new Insets(5, 0, 0, 10));
        // Создание vbox
        VBox vbAccount = new VBox(name, number, sum);
        vbAccount.getStyleClass().setAll("v-box");
        VBox.setMargin(vbAccount, new Insets(12, 0, 0, 0));
        vbAccount.setMinHeight(115);
        vbAccount.setMaxHeight(115);
        vbAccount.setMinWidth(358);
        vbAccount.setMaxWidth(358);
        // Добавить действие при нажатии
        vbAccount.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                AccountDTO account = new AccountDTO();
                for (AccountDTO tempAccount : ClientSocket.getInstance().getUser().getAccounts()) {
                    if (tempAccount.getAccountNumber().equals(accountDTO.getAccountNumber())) {
                        account = tempAccount;
                        break;
                    }
                }
                // Скрыть контекстное меню
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
                // Установка стилей
                for (int i = 1; i < vbAccounts.getChildren().size(); i++) {
                    vbAccounts.getChildren().get(i).getStyleClass().setAll("v-box");
                }
                vbAccount.getStyleClass().setAll("v-box-active");
                createCards(account);
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.getItems().setAll(createMenuItemEditAccount(account), createMenuItemDeleteAccount(account.getAccountNumber()));
                    contextMenu.show(stage, mouseEvent.getSceneX(), mouseEvent.getSceneY());
                }
            }
        });
        return vbAccount;
    }

    // Создание vbox "добавить счет"
    private VBox createAddAccountVb() {
        ImageView plusIcon = new ImageView(new Image("/images/icons/plus.png"));
        plusIcon.setFitHeight(55);
        plusIcon.setFitWidth(55);
        VBox vbAddAccount = new VBox(plusIcon);
        VBox.setMargin(plusIcon, new Insets(30, 0, 0, 151));
        vbAddAccount.getStyleClass().setAll("v-box");
        VBox.setMargin(vbAddAccount, new Insets(12, 0, 0, 0));
        vbAddAccount.setMinHeight(115);
        vbAddAccount.setMaxHeight(115);
        vbAddAccount.setMinWidth(358);
        vbAddAccount.setMaxWidth(358);
        vbAddAccount.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                addAccount();
            }
        });
        return vbAddAccount;
    }

    // Создание элемента "Редактировать" контекстного меню банковских счетов
    private MenuItem createMenuItemEditAccount(AccountDTO accountDTO) {
        MenuItem menuItem = new MenuItem("Редактировать");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                editAccount(accountDTO);
            }
        });
        menuItem.setStyle("-fx-font-size: 20");
        return menuItem;
    }

    // Создание элемента "Удалить" контекстного меню банковских счетов
    private MenuItem createMenuItemDeleteAccount(String accountNumber) {
        MenuItem menuItem = new MenuItem("Удалить");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AccountDTO account = new AccountDTO();
                for (AccountDTO tempAccount : ClientSocket.getInstance().getUser().getAccounts()) {
                    if (tempAccount.getAccountNumber().equals(accountNumber)) {
                        account = tempAccount;
                        break;
                    }
                }
                if (account.getCards() != null && account.getCards().size() != 0) {
                    Info info = new Info(stage, "Невозможно закрыть банковский счет", "Удалите банковские карты, привязанные к счету");
                    info.showAndWait();
                    return;
                }
                Choice choice = new Choice(stage, "Удаление банковского счета", "Вы уверены, что хотите закрыть банковский счет? Оставшиеся средства будут списаны в пользу банка.", "Закрыть счет", "Отмена");
                if (choice.showAndWait() == 1) {
                    try {
                        ClientSocket.getInstance().send(new Request(Request.RequestType.DeleteAccount, (new JSON<AccountDTO>()).toJson(account, AccountDTO.class)));
                        ClientSocket.getInstance().refreshUser();
                        createAccounts();
                    } catch (Exception e) {
                        Error error = new Error(stage, "Ошибка удаления банковского счета", e.getMessage());
                        error.showAndWait();
                    }
                }
            }
        });
        menuItem.setStyle("-fx-font-size: 20");
        return menuItem;
    }

    // Создание нового банковского счета
    @FXML
    private void addAccount() {
        AccountDTO newAccount = openCreateAccount();
        if (newAccount.getAccountName().equals("#none")) {
            return;
        }
        try {
            newAccount.setDateOfCreate(Date.valueOf(LocalDate.now().plusDays(1)));
            newAccount.setUser(ClientSocket.getInstance().getUser());
            ClientSocket.getInstance().send(new Request(Request.RequestType.CreateAccount, (new JSON<AccountDTO>()).toJson(newAccount, AccountDTO.class)));
            String message = ClientSocket.getInstance().get().getRequestMessage();
            switch (message) {
                case "true" -> {
                    ClientSocket.getInstance().refreshUser();
                }
                case "number" -> {
                    Error error = new Error(stage, "Не удалось добавить банковский счет", "Банковский счет с данным номером уже существует. Проверьте введенные данные.");
                    error.showAndWait();
                }
                case "name" -> {
                    Error error = new Error(stage, "Не удалось добавить банковский счет", "Банковский счет с данным названием уже существует. Придумайте новое название.");
                    error.showAndWait();
                }
            }
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка добавления банковского счета", e.getMessage());
            error.showAndWait();
        }
        createAccounts();
    }

    // Редактирование банковского счета
    private void editAccount(AccountDTO accountDTO) {
        AccountDTO newAccount = openEditAccount(accountDTO);
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.UpdateAccount, (new JSON<AccountDTO>()).toJson(newAccount, AccountDTO.class)));
            if (ClientSocket.getInstance().get().getRequestMessage().equals("true")) {
                ClientSocket.getInstance().refreshUser();
            }
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка редактирования банковского счета", e.getMessage());
            error.showAndWait();
        }
        createAccounts();
    }


    // Вызов окна создания банковского счета
    private AccountDTO openCreateAccount() {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/account/createAccount.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Открытие банковского счета");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        CreateAccountController controller = loader.<CreateAccountController>getController();
        controller.setStage(new_stage);
        new_stage.showAndWait();
        return controller.getAccount();
    }

    // Вызов окна редактирования банковского счета
    private AccountDTO openEditAccount(AccountDTO accountDTO) {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/account/editAccount.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Изменение банковского счета");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        EditAccountController controller = loader.<EditAccountController>getController();
        controller.setStage(new_stage);
        controller.setAccount(accountDTO);
        new_stage.showAndWait();
        return controller.getAccount();
    }


    // Вывод банковских карт, относящихся к банковскому счету
    private void createCards(AccountDTO accountDTO) {
        vbCards.getChildren().clear();
        vbHistory.setVisible(false);
        // Добавление карт счета на экран
        if (accountDTO.getCards() != null) {
            for (CardDTO cardDTO : accountDTO.getCards()) {
                vbCards.getChildren().add(createCardVb(cardDTO));
            }
        }
        // Создание заголовка
        Label lblMyCards = new Label("Мои карты");
        VBox.setMargin(lblMyCards, new Insets(0, 0, 0, 10));
        // Заполнение vbox дополнительными элементами
        if (accountDTO.getCards() != null && accountDTO.getCards().size() < 4) {
            vbCards.getChildren().add(createAddCardVb(accountDTO));
        }
        vbCards.getChildren().add(0, lblMyCards);
        // Открытие первой банковской карты
        if (accountDTO.getCards() != null && accountDTO.getCards().size() > 0) {
            vbCards.getChildren().get(1).getStyleClass().setAll("v-box-active");
            showCardInfo(accountDTO.getCards().get(0));
        }
    }

    // Создание vbox банковской карты
    private VBox createCardVb(CardDTO cardDTO) {
        // Создание текстового поля с номером карты
        Label number = new Label(cardDTO.getCardNumber().substring(0, 4) + " " + cardDTO.getCardNumber().substring(4, 8) + " **** " + cardDTO.getCardNumber().substring(12));
        number.getStyleClass().setAll("label-text");
        number.setStyle("-fx-font-size: 32");
        VBox.setMargin(number, new Insets(30, 0, 0, 20));
        // Создание текстового поля со сроком действия карты
        Label validity = new Label(cardDTO.getValidity());
        validity.getStyleClass().setAll("label-text");
        VBox.setMargin(validity, new Insets(20, 0, 0, 20));
        // Создание текстового поля с владельцем карты
        Label user = new Label(ClientSocket.getInstance().getUser().getName().toUpperCase() + " " + ClientSocket.getInstance().getUser().getSurname().toUpperCase());
        user.getStyleClass().setAll("label-text");
        VBox.setMargin(user, new Insets(10, 0, 0, 20));
        // Создание vbox
        VBox vbCard = new VBox(number, validity, user);
        vbCard.getStyleClass().setAll("v-box");
        VBox.setMargin(vbCard, new Insets(12, 0, 0, 0));
        vbCard.setMinHeight(200);
        vbCard.setMaxHeight(200);
        vbCard.setMinWidth(358);
        vbCard.setMaxWidth(358);
        // Добавить действие при нажатии
        vbCard.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                // Скрыть контекстное меню
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
                // Установка стилей
                for (int i = 1; i < vbCards.getChildren().size(); i++) {
                    vbCards.getChildren().get(i).getStyleClass().setAll("v-box");
                }
                vbCard.getStyleClass().setAll("v-box-active");
                showCardInfo(cardDTO);
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.getItems().setAll(createMenuItemDeleteCard(cardDTO));
                    contextMenu.show(stage, mouseEvent.getSceneX(), mouseEvent.getSceneY());
                }
            }
        });
        return vbCard;
    }

    // Создание vbox "добавить карту"
    private VBox createAddCardVb(AccountDTO accountDTO) {
        ImageView plusIcon = new ImageView(new Image("/images/icons/plus.png"));
        plusIcon.setFitHeight(55);
        plusIcon.setFitWidth(55);
        VBox vbAddCard = new VBox(plusIcon);
        VBox.setMargin(plusIcon, new Insets(72, 0, 0, 151));
        vbAddCard.getStyleClass().setAll("v-box");
        VBox.setMargin(vbAddCard, new Insets(12, 0, 0, 0));
        vbAddCard.setMinHeight(200);
        vbAddCard.setMaxHeight(200);
        vbAddCard.setMinWidth(358);
        vbAddCard.setMaxWidth(358);
        vbAddCard.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                addCard(accountDTO);
            }
        });
        return vbAddCard;
    }

    // Создание элемента "Удалить" контекстного меню банковских карт
    private MenuItem createMenuItemDeleteCard(CardDTO cardDTO) {
        MenuItem menuItem = new MenuItem("Удалить");
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Choice choice = new Choice(stage, "Удаление банковской карты", "Вы уверены, что хотите закрыть банковскую карту?", "Удалить карту", "Отмена");
                if (choice.showAndWait() == 1) {
                    try {
                        ClientSocket.getInstance().send(new Request(Request.RequestType.DeleteCard, (new JSON<CardDTO>()).toJson(cardDTO, CardDTO.class)));
                        ClientSocket.getInstance().refreshUser();
                        createAccounts();
                    } catch (Exception e) {
                        Error error = new Error(stage, "Ошибка удаления банковской карты", e.getMessage());
                        error.showAndWait();
                    }
                    AccountDTO account = new AccountDTO();
                    for (AccountDTO tempAccount : ClientSocket.getInstance().getUser().getAccounts()) {
                        if (tempAccount.getAccountNumber().equals(cardDTO.getAccount().getAccountNumber())) {
                            account = tempAccount;
                            break;
                        }
                    }
                    createCards(account);
                }
            }
        });
        menuItem.setStyle("-fx-font-size: 20");
        return menuItem;
    }

    // Создание новой банковской карты
    @FXML
    private void addCard(AccountDTO accountDTO) {
        CardDTO newCard = openCreateCard();
        if (newCard.getCardNumber().equals("#none")) {
            return;
        }
        newCard.setAccount(accountDTO);
        try {
            ClientSocket.getInstance().send(new Request(Request.RequestType.CreateCard, (new JSON<CardDTO>()).toJson(newCard, CardDTO.class)));
            if (ClientSocket.getInstance().get().getRequestMessage().equals("true")) {
                ClientSocket.getInstance().refreshUser();
            } else {
                Error error = new Error(stage, "Не удалось добавить банковскую карту", "Банковский карта с данным номером уже существует. Проверьте введенные данные.");
                error.showAndWait();
            }
        } catch (IOException e) {
            Error error = new Error(stage, "Ошибка создания банковской карты", e.getMessage());
            error.showAndWait();
        }
        AccountDTO account = new AccountDTO();
        for (AccountDTO tempAccount : ClientSocket.getInstance().getUser().getAccounts()) {
            if (tempAccount.getAccountNumber().equals(accountDTO.getAccountNumber())) {
                account = tempAccount;
                break;
            }
        }
        createCards(account);
    }


    // Вызов окна создания карты
    private CardDTO openCreateCard() {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/card/createCard.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Создание банковской карты");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        CreateCardController controller = loader.<CreateCardController>getController();
        controller.setStage(new_stage);
        new_stage.showAndWait();
        return controller.getCard();
    }


    // Вывод информации о банковской карте
    private void showCardInfo(CardDTO cardDTO) {
        vbHistory.setVisible(true);
        vbOperations.getChildren().clear();
        if (cardDTO.getOperations() == null || cardDTO.getOperations().size() == 0) {
            Label operations = new Label("Операции по карте отсутствуют");
            operations.getStyleClass().setAll("label-text");
            VBox.setMargin(operations, new Insets(20, 0, 0, 20));
            vbOperations.getChildren().setAll(operations);
            return;
        }
        ArrayList<OperationDTO> operations = (ArrayList<OperationDTO>) cardDTO.getOperations();
        for (int i = operations.size() - 1; i > operations.size() - 6 && i >= 0; i--) {
            Label operation = new Label(operations.get(i).getDate() + " " + operations.get(i).getTime() + "\n" + operations.get(i).getSum() + " руб.\n" + operations.get(i).getName());
            operation.getStyleClass().setAll("label-text");
            VBox.setMargin(operation, new Insets(20, 0, 0, 20));
            vbOperations.getChildren().add(operation);
        }
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
