package com.app.controller.userInterface;

import com.app.DTO.*;
import com.app.client.ClientSocket;
import com.app.controller.card.CardSelectionController;
import com.app.controller.launch.AuthorizationController;
import com.app.controller.user.IconsController;
import com.app.correctness.Correctness;
import com.app.mapper.JSON;
import com.app.messages.Choice;
import com.app.messages.Info;
import com.app.model.Request;
import com.app.pdf.PDF;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
import java.util.ArrayList;
import java.util.ResourceBundle;


@Setter
public class PaymentsController implements Initializable {
    @FXML
    private Button btnExit;
    @FXML
    private ImageView imageMenuMain, imageMenuTransfers, imageMenuPayments, imageMenuHistory, imageExitIcon, imageUserIcon, imagePlusSenderCard, imageAdminIcon;
    @FXML
    private VBox vbSenderCard;
    @FXML
    private TextField tfSenderCardSecurityCode, tfRecipient, tfRecipientAccount, tfRecipientBIC, tfRecipientUNP, tfSenderName, tfSenderAddress, tfPaymentName, tfPaymentSum, tfCardNumber, tfCardValidity;
    @FXML
    private StackPane spAdmin;

    private Label lblCardNumber;

    private Stage stage;  // основная сцена


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeImages();
        createTooltips();
    }

    // Инициализация изображений
    private void initializeImages() {
        imageMenuMain.setImage(new Image("/images/menuItemsIcons/main.png"));
        imageMenuTransfers.setImage(new Image("/images/menuItemsIcons/transfers.png"));
        imageMenuPayments.setImage(new Image("/images/menuItemsIcons/paymentsSelect.png"));
        imageMenuHistory.setImage(new Image("/images/menuItemsIcons/history.png"));
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
        imagePlusSenderCard.setImage(new Image("/images/icons/plus.png"));
        imageAdminIcon.setImage(new Image("/images/icons/admin.png"));
    }

    // Создание всплывающих подсказок
    private void createTooltips() {
        tfSenderCardSecurityCode.setTooltip(new Tooltip("Введите код безопасности CVV, состоящий из 3х цифр."));
        tfRecipient.setTooltip(new Tooltip("Введите получателя.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfRecipientAccount.setTooltip(new Tooltip("Введите номер счета получателя\nв формате LLNN LLLL NNNN NNNN NNNN NNNN NNNN без пробелов,\nгде L - большие буквы английского алфавита, а N - цифры."));
        tfRecipientBIC.setTooltip(new Tooltip("Введите BIC банка получателя, состоящий из 9ти цифр."));
        tfRecipientUNP.setTooltip(new Tooltip("Введите УНП получателя, состоящий из 9ти цифр."));
        tfSenderName.setTooltip(new Tooltip("Введите имя отправителя.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfSenderAddress.setTooltip(new Tooltip("Введите адрес отправителя.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfPaymentName.setTooltip(new Tooltip("Введите назначение платежа.\nИспользуйте для ввода буквы русского алфавита, символы: \" \" и \"-\"."));
        tfPaymentSum.setTooltip(new Tooltip("Введите сумму для перевода.\nИспользуйте для ввода цифры и символ \".\"."));
    }


    public void setUserIcon(UserDTO user) {
        imageUserIcon.setImage(new Image("/images/usersIcons/" + user.getImage() + ".png"));
        if (user.getRole().equals("admin")) {
            spAdmin.setVisible(true);
        }
    }


    // Выбор карты отправителя
    @FXML
    private void chooseSenderCard() {
        createVbCard(vbSenderCard);
    }

    // Создание vbox банковских карт
    private void createVbCard(VBox vb) {
        CardDTO card = openCardSelection();
        if (card.getCardNumber().equals("#none")) {
            return;
        }
        if (card.getCardNumber().equals("#empty")) {
            // Создание текстового поля с номером карты
            tfCardNumber = new TextField();
            tfCardNumber.setTooltip(new Tooltip("Введите номер карты, состоящий из 16ти цифр."));
            tfCardNumber.setPromptText("Номер банковской карты");
            tfCardNumber.setMaxHeight(30);
            tfCardNumber.setMinWidth(300);
            tfCardNumber.setMaxWidth(300);
            HBox hbNumber = new HBox(tfCardNumber);
            hbNumber.setMaxWidth(300);
            hbNumber.getStyleClass().setAll("h-box-field");
            VBox.setMargin(hbNumber, new Insets(20, 0, 0, 20));
            // Создание текстового поля со сроком действия карты
            tfCardValidity = new TextField();
            tfCardValidity.setTooltip(new Tooltip("Введите срок действия карты в формате MM/YY,\nгде MM - номер месяца, YY - номер года (две последние цифры)."));
            tfCardValidity.setPromptText("Срок действия банковской карты");
            tfCardValidity.setMaxHeight(30);
            tfCardValidity.setMinWidth(300);
            tfCardValidity.setMaxWidth(300);
            HBox hbValidity = new HBox(tfCardValidity);
            hbValidity.setMaxWidth(300);
            hbValidity.getStyleClass().setAll("h-box-field");
            VBox.setMargin(hbValidity, new Insets(20, 0, 0, 20));
            // Создание vbox
            vb.getChildren().setAll(hbNumber, hbValidity);
            lblCardNumber = null;
            return;
        }
        // Создание текстового поля с номером карты
        lblCardNumber = new Label(card.getCardNumber().substring(0, 4) + " " + card.getCardNumber().substring(4, 8) + " **** " + card.getCardNumber().substring(12));
        lblCardNumber.getStyleClass().setAll("label-text");
        lblCardNumber.setStyle("-fx-font-size: 32");
        VBox.setMargin(lblCardNumber, new Insets(30, 0, 0, 20));
        // Создание текстового поля со сроком действия карты
        Label lblCardValidity = new Label(card.getValidity());
        lblCardValidity.getStyleClass().setAll("label-text");
        VBox.setMargin(lblCardValidity, new Insets(20, 0, 0, 20));
        // Создание текстового поля с владельцем банковской карты
        Label lblCardUser = new Label(ClientSocket.getInstance().getUser().getName().toUpperCase() + " " + ClientSocket.getInstance().getUser().getSurname().toUpperCase());
        lblCardUser.getStyleClass().setAll("label-text");
        VBox.setMargin(lblCardUser, new Insets(10, 0, 0, 20));
        // Создание vbox
        vb.getChildren().setAll(lblCardNumber, lblCardValidity, lblCardUser);
        tfCardNumber = null;
        tfCardValidity = null;
    }

    // Открытие окна выбора банковской карты
    private CardDTO openCardSelection() {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/card/cardSelection.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Выбор банковской карты");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        CardSelectionController controller = loader.<CardSelectionController>getController();
        controller.setStage(new_stage);

        ArrayList<CardDTO> cards = new ArrayList<>();
        for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
            cards.addAll(account.getCards());
        }
        controller.setCards(cards, ClientSocket.getInstance().getUser().getName().toUpperCase() + " " + ClientSocket.getInstance().getUser().getSurname().toUpperCase());
        new_stage.showAndWait();
        return controller.getCard();
    }


    // Совершение платежа
    @FXML
    private void pay() {
        if (isPaymentInfoCorrect()) {
            // Создание платежа
            PaymentDTO payment = new PaymentDTO(tfPaymentName.getText().trim(), Double.parseDouble(tfPaymentSum.getText().trim()), null, tfRecipient.getText().trim(), tfRecipientUNP.getText().trim(), tfRecipientBIC.getText().trim(), tfRecipientAccount.getText().trim());
            // Если карта отправителя вводилась вручную
            if (tfCardNumber != null) {
                // Установить карту на пустую
                payment.setCard(new CardDTO());
                // Установить номер карты и срок действия из введенных данных
                payment.getCard().setCardNumber(tfCardNumber.getText().trim());
                payment.getCard().setValidity(tfCardValidity.getText().trim());
            }
            // Если карта отправителя выбрана
            else {
                // Установить карту пользователя
                for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
                    for (CardDTO card : account.getCards()) {
                        if (card.getCardNumber().substring(0, 4).equals(lblCardNumber.getText().substring(0, 4)) && card.getCardNumber().substring(4, 8).equals(lblCardNumber.getText().substring(5, 9)) && card.getCardNumber().substring(12, 16).equals(lblCardNumber.getText().substring(15, 19))) {
                            payment.setCard(card);
                            break;
                        }
                    }
                }
            }
            // Установить код безопасности карты
            payment.getCard().setSecurityCode(Integer.parseInt(tfSenderCardSecurityCode.getText().trim()));
            try {
                ClientSocket.getInstance().send(new Request(Request.RequestType.Payment, (new JSON<CardDTO>()).toJson(payment.getCard(), CardDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось совершить платеж", "Проверьте данные отправителя");
                    error.showAndWait();
                    return;
                }
                ClientSocket.getInstance().send(new Request(Request.RequestType.Payment, (new JSON<OperationDTO>()).toJson(new OperationDTO(payment), OperationDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось совершить платеж", "Недостаточно средств");
                    error.showAndWait();
                    return;
                }
                PDF pdf = new PDF();
                pdf.GeneratePdf(new OperationDTO(payment));
                pdf.run();
                Info info = new Info(stage, "Платеж выполнен успешно", "");
                info.showAndWait();
                ClientSocket.getInstance().send(new Request(Request.RequestType.Payment, (new JSON<UserDTO>()).toJson(ClientSocket.getInstance().getUser(), UserDTO.class)));
                ClientSocket.getInstance().refreshUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Проверка данных платежа на корректность
    private boolean isPaymentInfoCorrect() {
        boolean flag = true;
        if ((tfCardNumber == null && lblCardNumber == null)) {
            Error error = new Error(stage, "Не выбрана карта для платежа", "Выберите карту для совершения платежа");
            error.showAndWait();
            flag = false;
        }
        if (tfCardNumber != null) {
            tfCardNumber.getParent().getStyleClass().setAll("h-box-field");
            if (tfCardNumber.getText().trim().isBlank() || !Correctness.isCardNumberCorrect(tfCardNumber.getText().trim())) {
                tfCardNumber.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
            tfCardValidity.getParent().getStyleClass().setAll("h-box-field");
            if (tfCardValidity.getText().trim().isBlank() || !Correctness.isCardValidityCorrect(tfCardValidity.getText().trim())) {
                tfCardValidity.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
        }
        tfSenderCardSecurityCode.getParent().getStyleClass().setAll("h-box-field");
        if (tfSenderCardSecurityCode.getText().trim().isBlank() || !Correctness.isSecurityCodeCorrect(tfSenderCardSecurityCode.getText().trim())) {
            tfSenderCardSecurityCode.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfRecipient.getParent().getStyleClass().setAll("h-box-field");
        if (tfRecipient.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfRecipient.getText().trim())) {
            tfRecipient.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfRecipientAccount.getParent().getStyleClass().setAll("h-box-field");
        if (tfRecipientAccount.getText().trim().isBlank() || !Correctness.isBankAccountNumberCorrect(tfRecipientAccount.getText().trim())) {
            tfRecipientAccount.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfRecipientBIC.getParent().getStyleClass().setAll("h-box-field");
        if (tfRecipientBIC.getText().trim().isBlank() || !Correctness.isBICCorrect(tfRecipientBIC.getText().trim())) {
            tfRecipientBIC.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfRecipientUNP.getParent().getStyleClass().setAll("h-box-field");
        if (tfRecipientUNP.getText().trim().isBlank() || !Correctness.isBICCorrect(tfRecipientUNP.getText().trim())) {
            tfRecipientUNP.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfSenderName.getParent().getStyleClass().setAll("h-box-field");
        if (tfSenderName.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfSenderName.getText().trim())) {
            tfSenderName.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfSenderAddress.getParent().getStyleClass().setAll("h-box-field");
        if (tfSenderAddress.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfSenderAddress.getText().trim())) {
            tfSenderAddress.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfPaymentName.getParent().getStyleClass().setAll("h-box-field");
        if (tfPaymentName.getText().trim().isBlank() || !Correctness.isRussianWordCorrect(tfPaymentName.getText().trim())) {
            tfPaymentName.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfPaymentSum.getParent().getStyleClass().setAll("h-box-field");
        if (tfPaymentSum.getText().trim().isBlank() || !Correctness.isSumCorrect(tfPaymentSum.getText().trim())) {
            tfPaymentSum.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        return flag;
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
