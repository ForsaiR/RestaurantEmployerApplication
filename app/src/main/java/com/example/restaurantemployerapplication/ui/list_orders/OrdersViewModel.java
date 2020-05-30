package com.example.restaurantemployerapplication.ui.list_orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.restaurantemployerapplication.Application;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.example.restaurantemployerapplication.services.OrdersService;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrdersViewModel extends ViewModel {

    // Сервис для работы с заказами.
    private OrdersService ordersService;

    /**
     * Активные заказы (состояния {@link OrderStatus#PaymentMadeing}, {@link OrderStatus#Confirmed}
     * , {@link OrderStatus#Preparing}, {@link OrderStatus#Prepared})
     */
    private MutableLiveData<List<OrderModel>> activeOrdersSubject = new MutableLiveData<>();

    /**
     * Завершенные заказы заказы (состояния {@link OrderStatus#NoPlace}, {@link OrderStatus#PaymentError}
     * , {@link OrderStatus#Completed})
     */
    private MutableLiveData<List<OrderModel>> completedOrdersSubject = new MutableLiveData<>();

    /**
     * Все заказы системы.
     */
    private MutableLiveData<List<OrderModel>> allOrdersSubject = new MutableLiveData<>();

    /**
     * Subscriber для активных заказов.
     */
    private Disposable activeOrdersSubscriber;

    /**
     * Subscriber для завершенны заказов.
     */
    private Disposable completedOrdersSubscriber;

    /**
     * Subscriber для всех заказов.
     */
    private Disposable allOrdersSubscriber;

    public OrdersViewModel() {
        this(Application.getDaggerGraph().ordersService());
    }

    /**
     * Выбранные ресторан.
     */
    private MutableLiveData<OrderModel> selectedOrderSubject = new MutableLiveData<>();

    public OrdersViewModel(OrdersService ordersService) {
        this.ordersService = ordersService;
        refreshOrders();
    }

    /**
     * Получить активные заказы системы.
     *
     * @return observable.
     */
    public LiveData<List<OrderModel>> getActiveOrders() {
        return activeOrdersSubject;
    }

    /**
     * Получить завершенные заказы.
     */
    public LiveData<List<OrderModel>> getCompletedOrders() {
        return completedOrdersSubject;
    }

    public LiveData<List<OrderModel>> getAllOrders() {
        return allOrdersSubject;
    }

    private void initSubscribeAllOrders() {
        completedOrdersSubscriber = this.ordersService.getAllOrders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        orders -> this.allOrdersSubject.setValue(orders),
                        error -> {
                        }
                );
    }

    private void initSubscribeCompletedOrders() {
        completedOrdersSubscriber = this.ordersService.getCompletedOrders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        orders -> this.completedOrdersSubject.setValue(orders),
                        error -> {
                        }
                );
    }

    private void initSubscribeActiveOrders() {
        activeOrdersSubscriber = this.ordersService.getActiveOrderInRealTime()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addActiveOrders, error -> {
                    throw new RuntimeException(error);
                });
    }

    private void addActiveOrders(OrderModel order) {
        OrderModel replacedOrder = null;

        List<OrderModel> activeOrders = activeOrdersSubject.getValue();

        if (activeOrders == null) {
            activeOrders = new ArrayList<>();
        }

        // Try to find order with same id in cache
        try {
            replacedOrder = Observable.fromIterable(activeOrders)
                    .filter(preparingOrder -> preparingOrder.getId().equals(order.getId()))
                    .blockingFirst();
            int index = activeOrders.indexOf(replacedOrder);

            activeOrders.set(index, order);
        } catch (Exception ignored) {
            activeOrders.add(order);
        }

        activeOrdersSubject.setValue(new ArrayList<>(activeOrders));
    }

    private void removeActiveOrders(OrderModel order) {
        List<OrderModel> activeOrders = activeOrdersSubject.getValue();

        if (activeOrders == null) {
            activeOrders = new ArrayList<>();
        }

        if (activeOrders.contains(order)) {
            activeOrders.remove(order);
            activeOrdersSubject.setValue(new ArrayList<>(activeOrders));
        }
    }

    public void refreshOrders() {
        activeOrdersSubject.setValue(new ArrayList<>());
        completedOrdersSubject.setValue(new ArrayList<>());
        allOrdersSubject.setValue(new ArrayList<>());

        if (activeOrdersSubscriber != null) {
            activeOrdersSubscriber.dispose();
        }

        if (allOrdersSubscriber != null) {
            allOrdersSubscriber.dispose();
        }

        if (completedOrdersSubscriber != null) {
            completedOrdersSubscriber.dispose();
        }

        this.initSubscribeActiveOrders();
        this.initSubscribeCompletedOrders();
        this.initSubscribeAllOrders();
    }

    public void setSelectedOrder(OrderModel order) {
        selectedOrderSubject.setValue(order);
    }

    public LiveData<OrderModel> getSelectedOrder() {
        return selectedOrderSubject;
    }
}
