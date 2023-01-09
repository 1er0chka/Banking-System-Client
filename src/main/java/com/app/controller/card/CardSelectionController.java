package com.app.controller.card;

import com.app.DTO.CardDTO;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


@Setter
@Getter
public class CardSelectionController implements Initializable {
    @FXML
    private FlowPane fpCards;

    private Stage stage;  // сцена
    private CardDTO card = new CardDTO();  // выбранная банковская карта


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        card.setCardNumber("#none");
    }


    // Установка банковских карт
    public void setCards(ArrayList<CardDTO> cards, String userName) {
        fpCards.getChildren().clear();
        for (CardDTO cardDTO : cards) {
            fpCards.getChildren().add(createCardVb(cardDTO, userName));
        }
        fpCards.getChildren().add(createCardVb(new CardDTO(0, "Введите номер", "Введите срок действия", 0, null, null), "Введите имя пользователя"));
    }

    // Создание vbox банковской карты
    private VBox createCardVb(CardDTO cardDTO, String userName) {
        // Создание текстового поля с номером карты
        Label number;
        if (cardDTO.getCardNumber().equals("Введите номер")) {
            number = new Label(cardDTO.getCardNumber());
        } else {
            number = new Label(cardDTO.getCardNumber().substring(0, 4) + " " + cardDTO.getCardNumber().substring(4, 8) + " **** " + cardDTO.getCardNumber().substring(12));
        }
        number.getStyleClass().setAll("label-text");
        number.setStyle("-fx-font-size: 32");
        VBox.setMargin(number, new Insets(30, 0, 0, 20));
        // Создание текстового поля со сроком действия карты
        Label validity = new Label(cardDTO.getValidity());
        validity.getStyleClass().setAll("label-text");
        VBox.setMargin(validity, new Insets(20, 0, 0, 20));
        // Создание текстового поля с пользователем карты
        Label user = new Label(userName);
        user.getStyleClass().setAll("label-text");
        VBox.setMargin(user, new Insets(10, 0, 0, 20));
        // Создание vbox
        VBox vbCard = new VBox(number, validity, user);
        vbCard.getStyleClass().setAll("v-box");
        VBox.setMargin(vbCard, new Insets(20, 0, 0, 20));
        vbCard.setMinHeight(200);
        vbCard.setMaxHeight(200);
        vbCard.setMinWidth(358);
        vbCard.setMaxWidth(358);
        // Добавить действие при нажатии
        vbCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Установка стилей
                for (int i = 0; i < fpCards.getChildren().size(); i++) {
                    fpCards.getChildren().get(i).getStyleClass().setAll("v-box");
                }
                vbCard.getStyleClass().setAll("v-box-active");
                card = cardDTO;
            }
        });
        return vbCard;
    }


    // Подтверждение выбора банковской карты
    @FXML
    private void choose() {
        if (card.getCardNumber().equals("Введите номер")) {
            card.setCardNumber("#empty");
        }
        if (card.getCardNumber().equals("#none")) {
            Error error = new Error(stage, "Не выбрана карта", "Выберите карту для операции");
            error.showAndWait();
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Error error = new Error(stage, e.getMessage());
            error.showAndWait();
        }
        stage.close();
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
        card.setCardNumber("#none");
        stage.close();
    }
}
