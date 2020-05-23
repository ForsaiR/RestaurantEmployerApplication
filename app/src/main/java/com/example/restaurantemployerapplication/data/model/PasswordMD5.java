package com.example.restaurantemployerapplication.data.model;

import com.example.restaurantemployerapplication.services.Md5Service;

public class PasswordMD5 {

    private String passwordMd5;

    /**
     * Создание пароля с md5
     * @param password пароль в текстовом представлении без хэширования.
     */
    public PasswordMD5(String password) {
        this.passwordMd5 = Md5Service.md5Custom(password);
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }
}
