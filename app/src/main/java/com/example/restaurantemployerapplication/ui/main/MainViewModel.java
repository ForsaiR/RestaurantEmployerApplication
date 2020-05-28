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
    private OrdersService ordersService;
    private MutableLiveData<List<FullOrder>> preparingOrdersSubject = new MutableLiveData<>();
    private List<FullOrder> preparingOrders = new ArrayList<>();
    private Disposable subscriber;

    public MainViewModel() {
        Application.startWorking();

        authenticationService = Application.getDaggerGraph().authService();
        ordersService = Application.getDaggerGraph().ordersService();
        refresh();
    }

    LiveData<List<FullOrder>> preparingOrders() {
        return preparingOrdersSubject;
    }

    private void addPreparingOrders(FullOrder order) {
        FullOrder replacedOrder = null;

        // Try to find order with same id in cache
        try {
            replacedOrder = Observable.fromIterable(preparingOrders)
                    .filter(preparingOrder -> preparingOrder.getId().equals(order.getId()))
                    .blockingFirst();
            int index = preparingOrders.indexOf(replacedOrder);

            preparingOrders.set(index, order);
        } catch (Exception ignored) {
            preparingOrders.add(order);
        }

        preparingOrdersSubject.setValue(new ArrayList<>(preparingOrders));
    }

    private void removePreparingOrders(FullOrder order) {
        if (preparingOrders.contains(order)) {
            preparingOrders.remove(order);
            preparingOrdersSubject.setValue(new ArrayList<>(preparingOrders));
        }
    }

    private void initNewOrderGroup(GroupedObservable<Integer, OrderModel> orderGroup) {
        orderGroup.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                distinct().
                subscribe(order -> {
                    int i = 0;
                });
    }

    void logOut() {
        authenticationService.signOut();
    }

    void takeOrderToWork(FullOrder order) {
        this.ordersService.takeToWork(order).subscribeOn(Schedulers.io()).subscribe();
    }

    void prepareOrder(FullOrder order) {
        this.ordersService.prepareOrder(order).subscribeOn(Schedulers.io()).subscribe();
    }

    void completeOrder(FullOrder order) {
        this.ordersService.completeOrder(order).subscribeOn(Schedulers.io()).subscribe();
    }

    void refresh() {
        preparingOrders.clear();
        preparingOrdersSubject.setValue(new ArrayList<>(preparingOrders));
        if (subscriber != null) {
            subscriber.dispose();
        }

        initSubscribeOrders();
    }

    private void initSubscribeOrders() {
        subscriber = this.ordersService.preparingOrders().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addPreparingOrders, error -> {
                    throw new RuntimeException(error);
                });
    }
}
