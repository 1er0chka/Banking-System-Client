package com.app.controller.userInterface;

import com.app.DTO.*;
import com.app.client.ClientSocket;
import com.app.controller.account.AccountSelectionController;
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
public class TransfersController implements Initializable {
    @FXML
    private Button btnExit;
    @FXML
    private ImageView imageMenuMain, imageMenuTransfers, imageMenuPayments, imageMenuHistory, imageExitIcon, imageUserIcon, imagePlusSenderCard, imagePlusRecipientsCard, imagePlusSenderAccount, imagePlusRecipientsAccount, imageAdminIcon;
    @FXML
    private VBox vbSenderCard, vbRecipientsCard, vbSenderAccount, vbRecipientsAccount;
    @FXML
    private TextField tfSenderCardSecurityCode, tfSenderCardSum, tfSenderAccountSum;
    @FXML
    private StackPane spAdmin;

    private TextField tfSenderCardNumber, tfSenderCardValidity, tfRecipientsCardNumber, tfRecipientsCardValidity, tfSenderAccountNumber, tfRecipientsAccountNumber;
    private Label lblSenderCardNumber, lblRecipientCardNumber, lblRecipientsCardValidity, lblSenderAccountNumber, lblRecipientsAccountNumber;

    private Stage stage;  // основная сцена


    // Инициализация элементов окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeImages();
        tfSenderCardSecurityCode.setTooltip(new Tooltip("Введите код безопасности CVV, состоящий из 3х цифр."));
        tfSenderCardSum.setTooltip(new Tooltip("Введите сумму для перевода.\nИспользуйте для ввода цифры и символ \".\"."));
        tfSenderAccountSum.setTooltip(new Tooltip("Введите сумму для перевода.\nИспользуйте для ввода цифры и символ \".\"."));
    }

    // Инициализация изображений
    private void initializeImages() {
        imageMenuMain.setImage(new Image("/images/menuItemsIcons/main.png"));
        imageMenuTransfers.setImage(new Image("/images/menuItemsIcons/transfersSelect.png"));
        imageMenuPayments.setImage(new Image("/images/menuItemsIcons/payments.png"));
        imageMenuHistory.setImage(new Image("/images/menuItemsIcons/history.png"));
        imageExitIcon.setImage(new Image("/images/icons/logout.png"));
        imagePlusSenderCard.setImage(new Image("/images/icons/plus.png"));
        imagePlusRecipientsCard.setImage(new Image("/images/icons/plus.png"));
        imagePlusSenderAccount.setImage(new Image("/images/icons/plus.png"));
        imagePlusRecipientsAccount.setImage(new Image("/images/icons/plus.png"));
        imageAdminIcon.setImage(new Image("/images/icons/admin.png"));
    }


    // Установить менеджера
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

    // Выбор карты получателя
    @FXML
    private void chooseRecipientsCard() {
        createVbCard(vbRecipientsCard);
    }

    // Создание vbox банковских карт
    private void createVbCard(VBox vb) {
        CardDTO card = openCardSelection();
        if (card.getCardNumber().equals("#none")) {
            return;
        }
        if (card.getCardNumber().equals("#empty")) {
            // Создание текстового поля с номером карты
            TextField tfCardNumber = new TextField();
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
            TextField tfCardValidity = new TextField();
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
            if (vb.getId().equals("vbSenderCard")) {
                tfSenderCardNumber = tfCardNumber;
                tfSenderCardValidity = tfCardValidity;
                lblSenderCardNumber = null;
            } else {
                tfRecipientsCardNumber = tfCardNumber;
                tfRecipientsCardValidity = tfCardValidity;
                lblRecipientCardNumber = null;
                lblRecipientsCardValidity = null;
            }
            return;
        }
        // Создание текстового поля с номером карты
        Label lblCardNumber = new Label(card.getCardNumber().substring(0, 4) + " " + card.getCardNumber().substring(4, 8) + " **** " + card.getCardNumber().substring(12));
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
        if (vb.getId().equals("vbSenderCard")) {
            tfSenderCardNumber = null;
            tfSenderCardValidity = null;
            lblSenderCardNumber = lblCardNumber;
        } else {
            tfRecipientsCardNumber = null;
            tfRecipientsCardValidity = null;
            lblRecipientCardNumber = lblCardNumber;
            lblRecipientsCardValidity = lblCardValidity;
        }
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


    // Выбор счета отправителя
    @FXML
    private void chooseSenderAccount() {
        createVbAccount(vbSenderAccount);
    }

    // Выбор счета получателя
    @FXML
    private void chooseRecipientsAccount() {
        createVbAccount(vbRecipientsAccount);
    }

    // Создание vbox банковских счетов
    private void createVbAccount(VBox vb) {
        AccountDTO account = openAccountSelection();
        if (account.getAccountNumber().equals("#none")) {
            return;
        }
        if (account.getAccountNumber().equals("#empty")) {
            // Создание текстового поля с номером счета
            TextField tfAccountNumber = new TextField();
            tfAccountNumber.setTooltip(new Tooltip("Введите номер счета в формате LLNN LLLL NNNN NNNN NNNN NNNN NNNN без пробелов,\nгде L - большие буквы английского алфавита, а N - цифры."));
            tfAccountNumber.setPromptText("Номер банковского счета");
            tfAccountNumber.setMaxHeight(30);
            tfAccountNumber.setMinWidth(300);
            tfAccountNumber.setMaxWidth(300);
            HBox hbNumber = new HBox(tfAccountNumber);
            hbNumber.setMaxWidth(300);
            hbNumber.getStyleClass().setAll("h-box-field");
            VBox.setMargin(hbNumber, new Insets(20, 0, 0, 20));
            // Создание vbox
            vb.getChildren().setAll(hbNumber);
            if (vb.getId().equals("vbSenderAccount")) {
                tfSenderAccountNumber = tfAccountNumber;
                lblSenderAccountNumber = null;
            } else {
                tfRecipientsAccountNumber = tfAccountNumber;
                lblRecipientsAccountNumber = null;
            }
            return;
        }
        // Создание текстового поля с названием счета
        Label lblAccountName = new Label(account.getAccountName());
        lblAccountName.getStyleClass().setAll("label-text");
        VBox.setMargin(lblAccountName, new Insets(20, 0, 0, 20));
        // Создание текстового поля с номером счета
        Label lblAccountNumber = new Label(account.getAccountNumber());
        lblAccountNumber.getStyleClass().setAll("label-text");
        VBox.setMargin(lblAccountNumber, new Insets(10, 0, 0, 20));
        // Создание vbox
        vb.getChildren().setAll(lblAccountName, lblAccountNumber);

        if (vb.getId().equals("vbSenderAccount")) {
            tfSenderAccountNumber = null;
            lblSenderAccountNumber = lblAccountNumber;
        } else {
            tfRecipientsAccountNumber = null;
            lblRecipientsAccountNumber = lblAccountNumber;
        }
    }

    // Открытие окна выбора банковского счета
    private AccountDTO openAccountSelection() {
        Stage new_stage = new Stage(StageStyle.UNDECORATED);
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/app/account/accountSelection.fxml"));
            new_stage.setScene(new Scene((Pane) loader.load()));
        } catch (IOException ignored) {
        }
        new_stage.setTitle("Выбор банковского счета");
        new_stage.initModality(Modality.APPLICATION_MODAL);
        new_stage.initOwner(stage.getScene().getWindow());
        AccountSelectionController controller = loader.<AccountSelectionController>getController();
        controller.setStage(new_stage);
        controller.setAccounts((ArrayList<AccountDTO>) ClientSocket.getInstance().getUser().getAccounts());
        new_stage.showAndWait();
        return controller.getAccount();
    }


    // Выполнение перевода с карты на карту
    @FXML
    private void makeTransferCardToCard() {
        if (isCardToCardInfoCorrect()) {
            // Создание перевода с названием и суммой
            CardToCardDTO transfer = new CardToCardDTO("Перевод средств на карту", Double.parseDouble(tfSenderCardSum.getText().trim()), null, null, null);
            // Если карта отправителя вводилась вручную
            if (tfSenderCardNumber != null) {
                // Установить карту на пустую
                transfer.setCard(new CardDTO());
                // Установить номер карты и срок действия из введенных данных
                transfer.getCard().setCardNumber(tfSenderCardNumber.getText().trim());
                transfer.getCard().setValidity(tfSenderCardValidity.getText().trim());
            }
            // Если карта отправителя выбрана
            else {
                // Установить карту пользователя
                for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
                    for (CardDTO card : account.getCards()) {
                        if (card.getCardNumber().substring(0, 4).equals(lblSenderCardNumber.getText().substring(0, 4)) && card.getCardNumber().substring(4, 8).equals(lblSenderCardNumber.getText().substring(5, 9)) && card.getCardNumber().substring(12, 16).equals(lblSenderCardNumber.getText().substring(15, 19))) {
                            transfer.setCard(card);
                            break;
                        }
                    }
                }
            }
            // Установить код безопасности карты
            transfer.getCard().setSecurityCode(Integer.parseInt(tfSenderCardSecurityCode.getText().trim()));
            // Если карта получателя вводилась вручную
            if (tfRecipientsCardNumber != null) {
                // Установить номер карты и срок действия на введенный
                transfer.setRecipientCardNumber(tfRecipientsCardNumber.getText().trim());
                transfer.setRecipientCardValidity(tfRecipientsCardValidity.getText().trim());
            } else {
                // Установить номер карты и срок действия на выбранный
                for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
                    for (CardDTO card : account.getCards()) {
                        if (card.getCardNumber().substring(0, 4).equals(lblRecipientCardNumber.getText().substring(0, 4)) && card.getCardNumber().substring(4, 8).equals(lblRecipientCardNumber.getText().substring(5, 9)) && card.getCardNumber().substring(12, 16).equals(lblRecipientCardNumber.getText().substring(15, 19))) {
                            transfer.setRecipientCardNumber(card.getCardNumber());
                            break;
                        }
                    }
                }
                transfer.setRecipientCardValidity(lblRecipientsCardValidity.getText());
            }
            try {
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferCard, (new JSON<CardDTO>()).toJson(transfer.getCard(), CardDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось выполнить перевод", "Проверьте данные отправителя");
                    error.showAndWait();
                    return;
                }
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferCard, (new JSON<CardDTO>()).toJson(new CardDTO(0, transfer.getRecipientCardNumber(), transfer.getRecipientCardValidity(), 0, null, null), CardDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось выполнить перевод", "Проверьте данные получателя");
                    error.showAndWait();
                    return;
                }
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferCard, (new JSON<OperationDTO>()).toJson(new OperationDTO(transfer), OperationDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось выполнить перевод", "Недостаточно средств");
                    error.showAndWait();
                    return;
                }
                PDF pdf = new PDF();
                pdf.GeneratePdf(new OperationDTO(transfer));
                pdf.run();
                Info info = new Info(stage, "Перевод средств выполнен успешно", "");
                info.showAndWait();
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferCard, (new JSON<UserDTO>()).toJson(ClientSocket.getInstance().getUser(), UserDTO.class)));
                ClientSocket.getInstance().refreshUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Проверка корректности введенных данных при переводе с карты на карту
    private boolean isCardToCardInfoCorrect() {
        boolean flag = true;
        if ((tfSenderCardNumber == null && lblSenderCardNumber == null) || (tfRecipientsCardNumber == null && lblRecipientCardNumber == null)) {
            Error error = new Error(stage, "Не выбраны карты для перевода", "Выберите карту отправителя и карту получателя перевода");
            error.showAndWait();
            flag = false;
        }
        if (tfSenderCardNumber != null) {
            tfSenderCardNumber.getParent().getStyleClass().setAll("h-box-field");
            if (tfSenderCardNumber.getText().trim().isBlank() || !Correctness.isCardNumberCorrect(tfSenderCardNumber.getText().trim())) {
                tfSenderCardNumber.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
            tfSenderCardValidity.getParent().getStyleClass().setAll("h-box-field");
            if (tfSenderCardValidity.getText().trim().isBlank() || !Correctness.isCardValidityCorrect(tfSenderCardValidity.getText().trim())) {
                tfSenderCardValidity.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
        }
        if (tfRecipientsCardNumber != null) {
            tfRecipientsCardNumber.getParent().getStyleClass().setAll("h-box-field");
            if (tfRecipientsCardNumber.getText().trim().isBlank() || !Correctness.isCardNumberCorrect(tfRecipientsCardNumber.getText().trim())) {
                tfRecipientsCardNumber.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
            tfRecipientsCardValidity.getParent().getStyleClass().setAll("h-box-field");
            if (tfRecipientsCardValidity.getText().trim().isBlank() || !Correctness.isCardValidityCorrect(tfRecipientsCardValidity.getText().trim())) {
                tfRecipientsCardValidity.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
        }
        tfSenderCardSum.getParent().getStyleClass().setAll("h-box-field");
        if (tfSenderCardSum.getText().trim().isBlank() || !Correctness.isSumCorrect(tfSenderCardSum.getText().trim())) {
            tfSenderCardSum.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        tfSenderCardSecurityCode.getParent().getStyleClass().setAll("h-box-field");
        if (tfSenderCardSecurityCode.getText().trim().isBlank() || !Correctness.isSecurityCodeCorrect(tfSenderCardSecurityCode.getText().trim())) {
            tfSenderCardSecurityCode.getParent().getStyleClass().setAll("h-box-field-error");
            flag = false;
        }
        return flag;
    }

    // Выполнение перевода со счета на счет
    @FXML
    private void makeTransferAccountToAccount() {
        if (isAccountToAccountInfoCorrect()) {
            // Создание перевода с названием и суммой
            AccountToAccountDTO transfer = new AccountToAccountDTO("Перевод средств на счет", Double.parseDouble(tfSenderAccountSum.getText().trim()), null, null);
            // Если счет отправителя вводился вручную
            if (tfSenderAccountNumber != null) {
                // Установить счет на пустой
                transfer.setSenderAccount(new AccountDTO());
                // Установить номер счета из введенных данных
                transfer.getSenderAccount().setAccountNumber(tfSenderAccountNumber.getText().trim());
            }
            // Если счет отправителя выбран
            else {
                // Установить счет пользователя
                for (AccountDTO account : ClientSocket.getInstance().getUser().getAccounts()) {
                    if (account.getAccountNumber().equals(lblSenderAccountNumber.getText())) {
                        transfer.setSenderAccount(account);
                    }
                }
            }
            // Если счет получателя вводился вручную
            if (tfRecipientsAccountNumber != null) {
                // Установить номер счета на введенный
                transfer.setRecipientAccountNumber(tfRecipientsAccountNumber.getText().trim());
            } else {
                // Установить номер счета на выбранный
                transfer.setRecipientAccountNumber(lblRecipientsAccountNumber.getText());
            }
            try {
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferAccount, (new JSON<AccountDTO>()).toJson(transfer.getSenderAccount(), AccountDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось выполнить перевод", "Проверьте данные отправителя");
                    error.showAndWait();
                    return;
                }
                AccountDTO recipientAccount = new AccountDTO();
                recipientAccount.setAccountNumber(transfer.getRecipientAccountNumber());
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferAccount, (new JSON<AccountDTO>()).toJson(recipientAccount, AccountDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось выполнить перевод", "Проверьте данные получателя");
                    error.showAndWait();
                    return;
                }
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferAccount, (new JSON<OperationDTO>()).toJson(new OperationDTO(transfer), OperationDTO.class)));
                if (ClientSocket.getInstance().get().getRequestMessage().equals("false")) {
                    Error error = new Error(stage, "Не удалось выполнить перевод", "Недостаточно средств");
                    error.showAndWait();
                    return;
                }
                PDF pdf = new PDF();
                pdf.GeneratePdf(new OperationDTO(transfer));
                pdf.run();
                Info info = new Info(stage, "Перевод средств выполнен успешно", "");
                info.showAndWait();
                ClientSocket.getInstance().send(new Request(Request.RequestType.TransferAccount, (new JSON<UserDTO>()).toJson(ClientSocket.getInstance().getUser(), UserDTO.class)));
                ClientSocket.getInstance().refreshUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }


    }


    // Проверка корректности введенных данных при переводе со счета на счет
    private boolean isAccountToAccountInfoCorrect() {
        boolean flag = true;
        if (tfSenderAccountNumber != null) {
            tfSenderAccountNumber.getParent().getStyleClass().setAll("h-box-field");
            if (tfSenderAccountNumber.getText().trim().isBlank() || !Correctness.isBankAccountNumberCorrect(tfSenderAccountNumber.getText().trim())) {
                tfSenderAccountNumber.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
        }
        if (tfRecipientsAccountNumber != null) {
            tfRecipientsAccountNumber.getParent().getStyleClass().setAll("h-box-field");
            if (tfRecipientsAccountNumber.getText().trim().isBlank() || !Correctness.isBankAccountNumberCorrect(tfRecipientsAccountNumber.getText().trim())) {
                tfRecipientsAccountNumber.getParent().getStyleClass().setAll("h-box-field-error");
                flag = false;
            }
        }
        tfSenderAccountSum.getParent().getStyleClass().setAll("h-box-field");
        if (tfSenderAccountSum.getText().trim().isBlank() || !Correctness.isSumCorrect(tfSenderAccountSum.getText().trim())) {
            tfSenderAccountSum.getParent().getStyleClass().setAll("h-box-field-error");
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
