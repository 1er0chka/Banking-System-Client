package com.app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserDTO {
    private int id;  // ID пользователя
    private String login;  // логин пользователя
    private String password;  // пароль пользователя
    private String role;  // роль пользователя
    private String surname;  // фамилия пользователя
    private String name;  // имя пользователя
    private String secondName;  // отчество пользователя
    private Date dateOfBirth;  // дата рождения пользователя
    private String image;  // персональное изображение пользователя
    @JsonIgnore
    private List<AccountDTO> accounts;  // банковские счета пользователя


    // Пустой конструктор !не удалять!
    public UserDTO() {
        this.id = 0;
        this.login = "none";
        this.password = "none";
        this.role = "user";
        this.surname = "none";
        this.name = "none";
        this.secondName = "none";
        this.dateOfBirth = Date.valueOf(LocalDate.of(2000, 1, 1).plusDays(1));
        this.image = "userIconА";
        this.accounts = new ArrayList<>();
    }

    // Конструктор для авторизации
    public UserDTO(String login, String password) {
        this.id = 0;
        this.login = login;
        this.password = password;
        this.role = "user";
        this.surname = "none";
        this.name = "none";
        this.secondName = "none";
        this.dateOfBirth = Date.valueOf(LocalDate.of(2000, 1, 1).plusDays(1));
        this.image = "userIcon" + name.substring(0, 1);
        this.accounts = new ArrayList<>();
        //this.operations = new ArrayList<>();
    }

    // Конструктор для регистрации
    public UserDTO(String login, String password, String surname, String name, String secondName, LocalDate dateOfBirth) {
        this.login = login;
        this.password = password;
        this.role = "user";
        this.surname = surname.substring(0, 1).toUpperCase() + surname.substring(1).toLowerCase();
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        this.secondName = secondName.substring(0, 1).toUpperCase() + secondName.substring(1).toLowerCase();
        this.dateOfBirth = Date.valueOf(dateOfBirth.plusDays(1));
        this.image = "userIcon" + name.substring(0, 1).toUpperCase();
        this.accounts = new ArrayList<>();
    }
}
