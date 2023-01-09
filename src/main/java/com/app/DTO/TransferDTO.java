package com.app.DTO;

import java.sql.Date;
import java.sql.Time;


public abstract class TransferDTO extends OperationDTO {
    public TransferDTO(int id, Date date, Time time, String name, double sum, CardDTO card) {
        super(id, date, time, name, sum, card);
    }
}
