package com.app.DTO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PaymentDTO extends OperationDTO {
    private String recipient;   // получатель
    private String recipientUNP;   // UNP получателя
    private String recipientBIC;   // BIC получателя
    private String recipientAccountNumber;   // банковский счет получателя

    public PaymentDTO(String name, double sum, CardDTO card, String recipient, String recipientUNP, String recipientBIC, String recipientAccountNumber) {
        super(0, Date.valueOf(LocalDate.now().plusDays(1)), Time.valueOf(LocalTime.now()), name, sum, card);
        this.recipient = recipient;
        this.recipientUNP = recipientUNP;
        this.recipientBIC = recipientBIC;
        this.recipientAccountNumber = recipientAccountNumber;
    }
}
