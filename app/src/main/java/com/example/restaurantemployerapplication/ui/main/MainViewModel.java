package com.example.restaurantemployerapplication.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.restaurantemployerapplication.Application;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.example.restaurantemployerapplication.services.AuthenticationService;
import com.example.restaurantemployerapplication.services.OrdersService;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.GroupedObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private AuthenticationService authenticationService;

    public MainViewModel() {
        this(Application.getDaggerGraph().authService());
    }

    public MainViewModel(AuthenticationService authenticationService) {
        Application.startWorking();
        this.authenticationService = authenticationService;
    }

    void logOut() {
        authenticationService.signOut();
    }

//    void takeOrderToWork(FullOrder order) {
//        this.ordersService.takeToWork(order).subscribeOn(Schedulers.io()).subscribe();
//    }
//
//    void prepareOrder(FullOrder order) {
//        this.ordersService.prepareOrder(order).subscribeOn(Schedulers.io()).subscribe();
//    }
//
//    void completeOrder(FullOrder order) {
//        this.ordersService.completeOrder(order).subscribeOn(Schedulers.io()).subscribe();
//    }
//
//    public void setSelectedOrder(FullOrder order) {
//        selectedOrderSubject.setValue(order);
//    }
//
//    public LiveData<FullOrder> getSelectedOrder() {
//        return selectedOrderSubject;
//    }
}
