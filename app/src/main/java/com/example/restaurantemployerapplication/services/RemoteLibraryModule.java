package com.example.restaurantemployerapplication.services;

import com.tamagotchi.tamagotchiserverprotocol.RestaurantClient;
import com.tamagotchi.tamagotchiserverprotocol.routers.IAccountApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IAuthenticateApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IDishesApiService;
import com.tamagotchi.tamagotchiserverprotocol.services.IAuthenticateInfoService;

import dagger.Module;
import dagger.Provides;

@Module
public class RemoteLibraryModule {
    @Provides static IAccountApiService provideAccountApiService() {
        return RestaurantClient.getInstance().getAccountService();
    }

    @Provides static IAuthenticateApiService provideAuthenticateApiService() {
        return RestaurantClient.getInstance().getAuthenticateService();
    }

    @Provides static IDishesApiService provideDishesApiService() {
        return RestaurantClient.getInstance().getDishesService();
    }

    @Provides static IAuthenticateInfoService provideAuthenticateInfoService() {
        return RestaurantClient.getInstance().getAuthenticateInfoService();
    }
}
