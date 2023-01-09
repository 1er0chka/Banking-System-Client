package com.app.DTO;

import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CardToCardDTO extends TransferDTO {
    private String recipientCardNumber;   // номер банковской карты получателя
    private String recipientCardValidity;   // номер банковской карты получателя

    public CardToCardDTO(int id, Date date, Time time, String name, double sum, CardDTO card, String recipientCardNumber, String recipientCardValidity) {
        super(id, date, time, name, sum, card);
        this.recipientCardNumber = recipientCardNumber;
        this.recipientCardValidity = recipientCardValidity;
    }

    public CardToCardDTO(String name, double sum, CardDTO card, String recipientCardNumber, String recipientCardValidity) {
        super(0, Date.valueOf(LocalDate.now().plusDays(1)), Time.valueOf(LocalTime.now()), name, sum, card);
        this.recipientCardNumber = recipientCardNumber;
        this.recipientCardValidity = recipientCardValidity;
    }
}
