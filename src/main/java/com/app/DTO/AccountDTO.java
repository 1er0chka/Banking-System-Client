package com.app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private int id;  // ID банковского счета
    private String accountName;  // название банковского счета
    private String accountNumber;  // номер банковского счета
    private double amount;  // сумма на банковском счету
    private Date dateOfCreate;  // дата открытия банковского счета
    private UserDTO user;  // пользователь-владелец счета
    @JsonIgnore
    private List<CardDTO> cards;  // банковские карты
}
