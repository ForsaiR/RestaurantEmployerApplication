package com.example.restaurantemployerapplication.ui.order;

import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.restaurantemployerapplication.Application;
import com.example.restaurantemployerapplication.data.model.FullMenuItem;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.example.restaurantemployerapplication.data.model.FullVisitTime;
import com.example.restaurantemployerapplication.services.OrdersService;
import com.example.restaurantemployerapplication.services.RfcToCalendarConverter;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.RestaurantModel;
import com.tamagotchi.tamagotchiserverprotocol.models.TableModel;
import com.tamagotchi.tamagotchiserverprotocol.models.UserModel;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ViewOrderViewModel extends ViewModel {

    private static final String TAG = "ViewOrderViewModel";

    /**
     * Id заказа.
     */
    private MutableLiveData<Integer> orderIdSubject = new MutableLiveData<>();

    /**
     * Время посещений ресторана.
     */
    private MutableLiveData<FullVisitTime> visitTimeSubject = new MutableLiveData<>();

    /**
     * Состояние заказа.
     */
    private MutableLiveData<OrderStatus> orderStatusSubject = new MutableLiveData<>();

    /**
     * Состояние готовности персонала.
     */
    private MutableLiveData<StaffStatus> staffStatusSubject = new MutableLiveData<>();

    /**
     * Ресторан посещения.
     */
    private MutableLiveData<RestaurantModel> restaurantSubject = new MutableLiveData<>();

    /**
     * Кол-во мест, забронированных пользователем.
     */
    private MutableLiveData<Integer> numberPersonsSubject = new MutableLiveData<>();

    /**
     * Столик, забронированный пользователем.
     */
    private MutableLiveData<TableModel> tableSubject = new MutableLiveData<>();

    /**
     * Клиент заказа.
     */
    private MutableLiveData<UserModel> clientSubject = new MutableLiveData<>();

    /**
     * Сумма оплаты по заказу (может отсутсвовать в заказе, если меню не было выбрано).
     */
    private MutableLiveData<Integer> amountSubject = new MutableLiveData<>();

    /**
     * Меню заказа (может отсуствовать в заказе).
     */
    private MutableLiveData<List<FullMenuItem>> orderMenuSubject = new MutableLiveData<>();

    /**
     * В заказе было выбрано меню.
     */
    private MutableLiveData<Boolean> isOrderWithMenuSubject = new MutableLiveData<>(false);

    /**
     * Краткая информация о заказе (OrderModel) загружена.
     */
    private MutableLiveData<Boolean> isShortInfoLoadedSubject = new MutableLiveData<>(false);

    /**
     * Ошибка загрузки краткой информация о заказе (OrderModel).
     */
    private MutableLiveData<Boolean> isShortInfoErrorSubject = new MutableLiveData<>(false);

    /**
     * Полная информация о заказе (OrderModel) загружена.
     */
    private MutableLiveData<Boolean> isFullInfoLoadedSubject = new MutableLiveData<>(false);

    /**
     * Ошибка загрузки полной информация о заказе (OrderModel).
     */
    private MutableLiveData<Boolean> isFullInfoErrorSubject = new MutableLiveData<>(false);

    /**
     * Сервис для работы с заказами
     */
    private OrdersService ordersService;

    /**
     * Краткая информация о заказе.
     */
    private OrderModel shortOrder;

    /**
     * Полная информация о заказе.
     */
    private FullOrder fullOrder;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Ctr для VM провайдера.
     */
    public ViewOrderViewModel(Integer orderId) {
        this(orderId, Application.getDaggerGraph().ordersService());
    }

    /**
     * Ctr для тестов.
     *
     * @param ordersService сервис для работы с заказами.
     */
    public ViewOrderViewModel(Integer orderId, OrdersService ordersService) {
        this.ordersService = ordersService;
        initOrder(orderId);
    }

    /**
     * Получить заказ.
     */
    private void initOrder(Integer orderId) {
        this.orderIdSubject.setValue(orderId);

        Disposable shortOrderSubscriber = this.ordersService.getOrderById(this.orderIdSubject.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::initShortOrderInfo,
                        error -> {
                            isShortInfoErrorSubject.setValue(true);
                        });

        compositeDisposable.add(shortOrderSubscriber);
    }

    private void initShortOrderInfo(OrderModel order) {
        this.setShortOrderInfo(order);

        // Подписываемся на обновление заказа
        Disposable updateSubscriber = Observable.interval(5, TimeUnit.SECONDS)
                .flatMap(time -> this.ordersService.getOrderById(order.getId()).toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(this::setShortOrderInfo
                        , error -> {
                            Log.e(TAG, "Update order error", error);
                        });

        compositeDisposable.add(updateSubscriber);

        // Получаем полную информацию о заказе, если она отсутсвует.
        Disposable fullOrderSubscriber = this.ordersService.getFullOrder(this.shortOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::initFullOrderInfo,
                        error -> {
                            isFullInfoErrorSubject.setValue(true);
                            Log.e(TAG, "Can't get full order", error);
                        }
                );

        compositeDisposable.add(fullOrderSubscriber);
    }

    private void setShortOrderInfo(OrderModel order) {
        // Если обновлений нет, то пропускаем.
        if (this.shortOrder != null && this.shortOrder.equals(order)) return;

        this.shortOrder = order;
        isShortInfoLoadedSubject.setValue(true);

        if (order.getMenu() != null && order.getMenu().size() != 0) {
            this.isOrderWithMenuSubject.setValue(true);
            this.amountSubject.setValue(shortOrder.getTotalAmount());
        }

        // TODO: не идеальный алгоритм, поправить. Если статусы не будут одинаквыми, то так и останется Notifying.
        StaffStatus staffStatus = StaffStatus.Notifying;
        if (order.getOrderCooksStatus() == StaffStatus.Notified && order.getOrderWaitersStatus() == StaffStatus.Notified) {
            staffStatus = StaffStatus.Notified;
        }

        if (order.getOrderCooksStatus() == StaffStatus.Pending || order.getOrderWaitersStatus() == StaffStatus.Pending) {
            staffStatus = StaffStatus.Pending;
        }

        if (order.getOrderCooksStatus() == StaffStatus.Ready && order.getOrderWaitersStatus() == StaffStatus.Ready) {
            staffStatus = StaffStatus.Ready;
        }

        this.staffStatusSubject.setValue(staffStatus);
        this.orderStatusSubject.setValue(order.getOrderStatus());

        this.numberPersonsSubject.setValue(order.getNumberOfPersons());

        FullVisitTime fullVisitTime = new FullVisitTime(order.getVisitTime());
        this.visitTimeSubject.setValue(fullVisitTime);
    }

    private void initFullOrderInfo(FullOrder order) {
        this.fullOrder = order;
        this.restaurantSubject.setValue(order.getRestaurant());
        this.clientSubject.setValue(order.getClient());
        this.orderMenuSubject.setValue(order.getMenu());
        this.tableSubject.setValue(order.getReservedTable());
        this.isFullInfoLoadedSubject.setValue(true);
    }

    /**
     * Осо
     */
    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    /**
     * Получить Observable на id заказа.
     *
     * @return Observable
     */
    public LiveData<Integer> getOrderIdObservable() {
        return orderIdSubject;
    }

    /**
     * Получить Observable на id заказа.
     *
     * @return Observable
     */
    public LiveData<FullVisitTime> getVisitTimeObservable() {
        return visitTimeSubject;
    }

    /**
     * Получить Observable на статус заказа.
     *
     * @return Observable
     */
    public LiveData<OrderStatus> getOrderStatusObservable() {
        return orderStatusSubject;
    }

    /**
     * Получить Observable на статус персонала по заказу.
     *
     * @return Observable
     */
    public LiveData<StaffStatus> getStaffStatusObservable() {
        return staffStatusSubject;
    }

    /**
     * Получить Observable на ресторан в который поступил заказ.
     *
     * @return Observable
     */
    public LiveData<RestaurantModel> getRestaurantObservable() {
        return restaurantSubject;
    }

    /**
     * Получить Observable кол-во мест в заказе.
     *
     * @return Observable
     */
    public LiveData<Integer> getNumberPersonsObservable() {
        return numberPersonsSubject;
    }

    /**
     * Получить Observable на столик заказа.
     *
     * @return Observable
     */
    public LiveData<TableModel> getTableObservable() {
        return tableSubject;
    }

    /**
     * Получить Observable на клиента, сделавшего заказ.
     *
     * @return Observable
     */
    public LiveData<UserModel> getClientObservable() {
        return clientSubject;
    }

    /**
     * Получить Observable на сумму заказа.
     *
     * @return Observable
     */
    public LiveData<Integer> getAmountObservable() {
        return amountSubject;
    }

    /**
     * Получить Observable на меню заказа.
     *
     * @return Observable
     */
    public LiveData<List<FullMenuItem>> getOrderMenuObservable() {
        return orderMenuSubject;
    }

    /**
     * Получить Observable на наличие заказанного меню в заказе.
     * Если false, то смысла запрашивать меню и сумму заказа нет.
     *
     * @return Observable
     */
    public LiveData<Boolean> getIsOrderWithMenuObservable() {
        return isOrderWithMenuSubject;
    }

    /**
     * Получить Observable на готовность краткой инфорации по заказу.
     *
     * @return Observable
     */
    public LiveData<Boolean> getIsShortInfoLoadedObservable() {
        return isShortInfoLoadedSubject;
    }

    /**
     * Получить Observable на ошибку при загрузки краткой информации по заказу.
     *
     * @return Observable
     */
    public LiveData<Boolean> getIsShortInfoErrorObservable() {
        return isShortInfoErrorSubject;
    }

    /**
     * Получить Observable на готовность полной инфорации по заказу.
     *
     * @return Observable
     */
    public LiveData<Boolean> getIsFullInfoLoadedObservable() {
        return isFullInfoLoadedSubject;
    }

    /**
     * Получить Observable на ошибку при загрузки полной информации по заказу.
     *
     * @return Observable
     */
    public LiveData<Boolean> getIsFullInfoErrorObservable() {
        return isFullInfoErrorSubject;
    }

    public void prepareOrder() {
        this.ordersService.prepareOrder(this.shortOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderUpdate -> {
                }, error -> {
                    Log.e(TAG, "Failed to prepare order", error);
                });
    }

    public void takeOrderToWork() {
        this.ordersService.takeToWork(this.shortOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderUpdate -> {
                }, error -> {
                    Log.e(TAG, "Failed to accept work order", error);
                });
    }

    public void completeOrder() {
        this.ordersService.completeOrder(this.shortOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderUpdate -> {
                }, error -> {
                    Log.e(TAG, "Failed to complete order", error);
                });
    }
}
