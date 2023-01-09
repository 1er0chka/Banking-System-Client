package com.app.DTO;


import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class AccountToAccountDTO extends TransferDTO {
    private AccountDTO senderAccount;   // банковский счет отправителя
    private String recipientAccountNumber;   // номер банковского счета получателя

    public AccountToAccountDTO(int id, Date date, Time time, String name, double sum, String recipientAccountNumber, AccountDTO senderAccount) {
        super(id, date, time, name, sum, null);
        this.recipientAccountNumber = recipientAccountNumber;
        this.senderAccount = senderAccount;
    }

    public AccountToAccountDTO(String name, double sum, String recipientAccountNumber, AccountDTO senderAccount) {
        super(0, Date.valueOf(LocalDate.now().plusDays(1)), Time.valueOf(LocalTime.now()), name, sum, null);
        this.recipientAccountNumber = recipientAccountNumber;
        this.senderAccount = senderAccount;
    }
}
