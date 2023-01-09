package com.app.correctness;

import java.time.LocalDate;

public class Correctness {
    // Проверка логина на корректность
    public static boolean isLoginCorrect(String login) {
        // Соответствует ли требуемой длине
        if (login.length() < 8 || login.length() > 20) {
            return false;
        }
        // Соответствует ли допустимым символам
        if (!login.matches("[a-zA-Z0-9_.-]+")) {
            return false;
        }
        // Содержит ли хотя бы одну строчную и одну заглавную буквы
        return !login.toLowerCase().equals(login) && !login.toUpperCase().equals(login);
    }

    // Проверка пароля на корректность
    public static boolean isPasswordCorrect(String password) {
        // Соответствует ли требуемой длине
        if (password.length() < 8 || password.length() > 20) {
            return false;
        }
        // Соответствует ли допустимым символам
        if (!password.matches("[a-zA-Z0-9]+")) {
            return false;
        }
        // Содержит ли хотя бы одну строчную и одну заглавную буквы
        return !password.toLowerCase().equals(password) && !password.toUpperCase().equals(password);
    }


    // Проверка русских слов на корректность
    public static boolean isRussianWordCorrect(String word) {
        // Соответствует ли допустимым символам
        return word.matches("[а-яА-Я -]+");
    }

    // Проверка даты на корректность
    public static boolean isDateCorrect(String date) {
        // Получение элементов даты
        String[] dates = date.split("\\.", 3);
        try {
            // Являются ли элементы целыми числами
            int day = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int year = Integer.parseInt(dates[2]);
            // Возможен ли введенный год
            if (year < 1920) {
                return false;
            }
            LocalDate localDate = LocalDate.of(year, month, day);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Проверка денежной суммы на корректность
    public static boolean isSumCorrect(String sum) {
        try {
            double summ = Double.parseDouble(sum);
            if (summ < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        if (sum.split("\\.").length == 2) {
            return sum.split("\\.")[1].length() <= 2;
        } else {
            return true;
        }
    }


    // Проверка номера банковского счета на корректность
    public static boolean isBankAccountNumberCorrect(String number) {
        if (number.length() != 28) {
            return false;
        }
        String codeCountry = number.substring(0, 2);
        String checkNumber = number.substring(2, 4);
        String bankCode = number.substring(4, 8);
        String balanceAccount = number.substring(8, 12);
        String individualAccount = number.substring(12);
        // Соответствуют ли код страны и код банка допустимым символам
        if (!codeCountry.matches("[A-Z]+") || !bankCode.matches("[A-Z]+")) {
            return false;
        }
        try {
            Integer.parseInt(checkNumber);
            Integer.parseInt(balanceAccount);
            Long.parseLong(individualAccount);
        } catch (NumberFormatException e) {
            return false;
        }
        return Integer.parseInt(checkNumber) >= 0 && Integer.parseInt(balanceAccount) >= 0 && Long.parseLong(individualAccount) >= 0;
    }


    // Проверка BIC на корректность
    public static boolean isBICCorrect(String bic) {
        if (bic.length() != 9) {
            return false;
        }
        try {
            Integer.parseInt(bic);
        } catch (NumberFormatException e) {
            return false;
        }
        return Integer.parseInt(bic) >= 0;
    }


    // Проверка номера банковской карты на корректность
    public static boolean isCardNumberCorrect(String number) {
        if (number.length() != 16) {
            return false;
        }
        try {
            Integer.parseInt(number.substring(0, 8));
            Integer.parseInt(number.substring(8));
        } catch (NumberFormatException e) {
            return false;
        }
        return Integer.parseInt(number.substring(0, 8)) >= 0 && Integer.parseInt(number.substring(8)) >= 0;
    }

    // Проверка срока действия банковской карты на корректность
    public static boolean isCardValidityCorrect(String validity) {
        if (validity.length() != 5) {
            return false;
        }
        try {
            int month = Integer.parseInt(validity.split("/")[0]);
            int year = Integer.parseInt(validity.split("/")[1]);
            if (month <= 0 || month > 13) {
                return false;
            }
            if (year < 22 || year > 40) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Проверка кода безопасности банковской карты на корректность
    public static boolean isSecurityCodeCorrect(String code) {
        if (code.length() != 3) {
            return false;
        }
        try {
            Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return Integer.parseInt(code) >= 0;
    }
}
