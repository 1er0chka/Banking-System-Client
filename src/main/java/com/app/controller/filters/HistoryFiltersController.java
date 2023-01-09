package com.app.controller.filters;

import com.app.correctness.Correctness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import com.app.messages.Error;

import java.net.URL;
import java.util.ResourceBundle;


@Setter
@Getter
public class HistoryFiltersController implements Initializable {
    @FXML
    private TextField tfMinSum, tfMaxSum;
    @FXML
    private ComboBox<String> cmPeriod;

    private Stage stage;  // сцена
    private boolean flag = true;


    // Инициализация окна
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmPeriod.setItems(FXCollections.observableArrayList("За час", "За сутки",  "За неделю", "За месяц", "За все время"));
        cmPeriod.getSelectionModel().selectLast();
        createTooltips();
    }

    // Создание всплывающих подсказок
    private void createTooltips() {
        tfMinSum.setTooltip(new Tooltip("Введите минимальную сумму операции.\nИспользуйте для ввода цифры и символ \".\"."));
        tfMaxSum.setTooltip(new Tooltip("Введите максимальную сумму операции.\nИспользуйте для ввода цифры и символ \".\"."));
    }


    public void setMin(String sum){
        tfMinSum.setText(sum);
    }

    public void setMax(String sum){
        tfMaxSum.setText(sum);
    }

    public void setPeriod(String period){
        cmPeriod.getSelectionModel().select(period);
    }

    public String getMin(){
        return tfMinSum.getText().trim();
    }

    public String getMax(){
        return tfMaxSum.getText().trim();
    }

    public String getPeriod(){
        return cmPeriod.getSelectionModel().getSelectedItem();
    }


    @FXML
    private void apply() {
        boolean flag = true;
        tfMinSum.getParent().getStyleClass().setAll("h-box");
        if (tfMinSum.getText().trim().isBlank()) {
            tfMinSum.setText("");
        } else {
            if (!Correctness.isSumCorrect(tfMinSum.getText().trim())) {
                tfMinSum.getParent().getStyleClass().setAll("h-box-error");
                flag = false;
            }
        }
        tfMaxSum.getParent().getStyleClass().setAll("h-box");
        if (tfMaxSum.getText().trim().isBlank()) {
            tfMaxSum.setText("");
        } else {
            if (!Correctness.isSumCorrect(tfMaxSum.getText().trim())) {
                tfMaxSum.getParent().getStyleClass().setAll("h-box-error");
                flag = false;
            }
            else {
                if (!tfMinSum.getText().trim().isBlank() && Correctness.isSumCorrect(tfMinSum.getText().trim()) && (Double.parseDouble(tfMaxSum.getText().trim()) < Double.parseDouble(tfMinSum.getText().trim()))) {
                    tfMaxSum.getParent().getStyleClass().setAll("h-box-error");
                    flag = false;
                }
            }
        }
        if (flag) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Error error = new Error(stage, e.getMessage());
                error.showAndWait();
            }
            stage.close();
        }
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
        flag = false;
        stage.close();
    }
}
