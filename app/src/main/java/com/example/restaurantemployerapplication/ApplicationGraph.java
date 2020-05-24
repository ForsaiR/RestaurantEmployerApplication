package com.example.restaurantemployerapplication;

import com.example.restaurantemployerapplication.services.AuthenticationService;
import com.example.restaurantemployerapplication.services.RemoteLibraryModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RemoteLibraryModule.class)
public interface ApplicationGraph {
    AuthenticationService authService();
}
