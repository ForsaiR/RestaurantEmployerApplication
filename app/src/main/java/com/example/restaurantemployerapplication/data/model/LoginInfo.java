package com.example.restaurantemployerapplication.data.model;

/**
 * POJO класс, содержащий информацию о логине и пароле для авторизации.
 */
public class LoginInfo {

    private String login;

    private PasswordMD5 password;

    /**
     * Создает сущность данных для аутентификации на сервере.
     * @param login Логин пользователя.
     * @param password Пароль пользователя в текстовом виде.
     */
    public LoginInfo(String login, String password) {
        this.login = login;
        this.password = new PasswordMD5(password);
    }

    /**
     * Возвращает md5 представление пароля.
     * @return пароль в md5.
     */
    public PasswordMD5 getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }
}
