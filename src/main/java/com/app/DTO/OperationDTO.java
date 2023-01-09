package com.app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationDTO {
    protected int id;  // ID операции
    protected Date date;  // дата совершения операции
    protected Time time;  // время совершения операции
    protected String name;  // название операции
    protected double sum;  // сумма операции
    protected CardDTO card;   // карта, с которой совершена операция

    public OperationDTO(TransferDTO transfer){
        id = 0;
        date = transfer.getDate();
        time = transfer.getTime();
        name = transfer.getName();
        sum = transfer.getSum();
        card = transfer.getCard();
    }

    public OperationDTO(PaymentDTO payment){
        id = 0;
        date = payment.getDate();
        time = payment.getTime();
        name = payment.getName();
        sum = payment.getSum();
        card = payment.getCard();
    }
}