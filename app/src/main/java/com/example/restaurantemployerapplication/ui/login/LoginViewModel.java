package com.example.restaurantemployerapplication.ui.login;

import androidx.lifecycle.ViewModel;

import com.example.restaurantemployerapplication.data.model.LoginInfo;
import com.example.restaurantemployerapplication.services.AuthenticationService;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    private AuthenticationService authenticationService;

    @Inject
    public LoginViewModel(AuthenticationService authenticationService) {

        this.authenticationService = authenticationService;
    }

    public void login(String login, String password) {
        authenticationService.signIn(new LoginInfo(login, password))
        .subscribe();
    }
}
