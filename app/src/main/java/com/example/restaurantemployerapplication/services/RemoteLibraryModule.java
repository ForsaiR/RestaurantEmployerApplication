package com.example.restaurantemployerapplication.services;

import com.tamagotchi.tamagotchiserverprotocol.RestaurantClient;
import com.tamagotchi.tamagotchiserverprotocol.routers.IAccountApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IAuthenticateApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IDishesApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IMenuApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IOrdersApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IRestaurantsApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.ITablesApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IUsersApiService;
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

    @Provides static IOrdersApiService provideOrdersApiService() {
        return RestaurantClient.getInstance().getOrdersApiService();
    }

    @Provides static IMenuApiService provideMenuApiService() {
        return RestaurantClient.getInstance().getMenuService();
    }

    @Provides static IRestaurantsApiService provideRestaurantsApiService() {
        return RestaurantClient.getInstance().getRestaurantsService();
    }

    @Provides static IUsersApiService provideUsersApiService() {
        return RestaurantClient.getInstance().getUsersService();
    }

    @Provides static ITablesApiService provideTablesApiService() {
        return RestaurantClient.getInstance().getTablesApiService();
    }
}
