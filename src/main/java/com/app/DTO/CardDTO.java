package com.app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    private int id;  // id банковской карты
    private String cardNumber;  // номер банковской карты
    private String validity;  // срок действия банковской карты
    private int securityCode;  // код безопасности банковской карты
    private AccountDTO account;  // банковский счет
    @JsonIgnore
    private List<OperationDTO> operations;  // операции
}
