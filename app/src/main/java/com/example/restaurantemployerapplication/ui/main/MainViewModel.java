package com.example.restaurantemployerapplication.ui.main;

import androidx.lifecycle.ViewModel;

import com.example.restaurantemployerapplication.Application;
import com.example.restaurantemployerapplication.services.AuthenticationService;

public class MainViewModel extends ViewModel {
    private AuthenticationService authenticationService;

    public MainViewModel() {
        authenticationService = Application.getDaggerGraph().authService();
        Application.startWorking();
    }

    public void logOut() {
        authenticationService.signOut();
    }
}
