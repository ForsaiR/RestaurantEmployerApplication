package com.example.restaurantemployerapplication.services;

import android.util.Log;

import com.example.restaurantemployerapplication.data.model.FullMenuItem;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.tamagotchi.tamagotchiserverprotocol.models.MenuItem;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderPathModel;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;
import com.tamagotchi.tamagotchiserverprotocol.routers.IDishesApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IMenuApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IOrdersApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IRestaurantsApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.ITablesApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IUsersApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class OrdersService {

    private static final int updateInterval = 15;
    private String LogTag = "OrdersService";

    private final IOrdersApiService ordersApiService;
    private final IMenuApiService menuApiService;
    private final IDishesApiService dishesApiService;
    private IRestaurantsApiService restaurantsApiService;
    private ITablesApiService tablesApiService;
    private IUsersApiService usersApiService;

    @Inject
    OrdersService(IOrdersApiService ordersApiService,
                  IMenuApiService menuApiService,
                  IDishesApiService dishesApiService,
                  IRestaurantsApiService restaurantsApiService,
                  ITablesApiService tablesApiService,
                  IUsersApiService usersApiService) {
        this.ordersApiService = ordersApiService;
        this.menuApiService = menuApiService;
        this.dishesApiService = dishesApiService;
        this.restaurantsApiService = restaurantsApiService;
        this.tablesApiService = tablesApiService;
        this.usersApiService = usersApiService;
    }

    /**
     * Получать активные заказы в системе в реальном времени (обновление каждый 15 секунд)
     *
     * @return Observable на заказы
     */
    public Observable<OrderModel> getActiveOrderInRealTime() {
        long firstEmit = 0;
        return Observable.interval(updateInterval, TimeUnit.SECONDS, Schedulers.io())
                .startWithItem(firstEmit)
                .flatMap(time -> getActiveOrders().toObservable().flatMapIterable(itemOrders -> itemOrders))
                .distinct();
    }

    /**
     * Получить активные заказы системы.
     *
     * @return Single на коллекци заказов
     */
    public Single<List<OrderModel>> getActiveOrders() {
        return ordersApiService.
                getAllOrders(null, OrderStatus.Confirmed, null, null)
                .concatWith(
                        ordersApiService.getAllOrders(null, OrderStatus.Preparing, null, null)
                )
                .concatWith(
                        ordersApiService.getAllOrders(null, OrderStatus.Prepared, null, null)
                )
                .concatWith(
                        ordersApiService.getAllOrders(null, OrderStatus.PaymentMadeing, null, null)
                ).reduce((x, y) -> {
                    List<OrderModel> orders = new ArrayList<>();
                    orders.addAll(x);
                    orders.addAll(y);
                    return orders;
                }).toSingle();
    }

    /**
     * Получать завершенные заказы в системе в реальном времени (обновление каждый 15 секунд)
     *
     * @return Observable на заказы
     */
    public Observable<OrderModel> getCompletedOrderInRealTime() {
        long firstEmit = 0;
        return Observable.interval(updateInterval, TimeUnit.SECONDS, Schedulers.io())
                .startWithItem(firstEmit)
                .flatMap(time -> getCompletedOrders().toObservable().flatMapIterable(itemOrders -> itemOrders))
                .distinct();
    }

    /**
     * Получить завершенные заказы системы.
     *
     * @return Single на коллекци заказов
     */
    public Single<List<OrderModel>> getCompletedOrders() {
        return ordersApiService.
                getAllOrders(null, OrderStatus.PaymentError, null, null)
                .mergeWith(
                        ordersApiService.getAllOrders(null, OrderStatus.NoPlace, null, null)
                )
                .mergeWith(
                        ordersApiService.getAllOrders(null, OrderStatus.Completed, null, null)
                ).reduce((x, y) -> {
                    List<OrderModel> orders = new ArrayList<>();
                    orders.addAll(x);
                    orders.addAll(y);
                    return orders;
                }).toSingle();
    }

    /**
     * Получить все заказы системы.
     *
     * @return Single на коллекци заказов
     */
    public Single<List<OrderModel>> getAllOrders() {
        return ordersApiService.
                getAllOrders(null, null, null, null);
    }

    /**
     * Получить заказ по Id
     */
    public Single<OrderModel> getOrderById(Integer orderId) {
        return ordersApiService.getOrderById(orderId);
    }


    /**
     * Заполнить сущность заказа.
     *
     * @param orderModel краткая информация о заказе, который нужно заполнить.
     * @return Observable на заполненную сущность заказа.
     */
    public Single<FullOrder> getFullOrder(OrderModel orderModel) {
        return Single.just(orderModel)
                .map(orderShortInfo -> new FullOrder(orderModel))
                // Получаем ресторана, в котором сделан заказ.
                .flatMap(fullOrder -> restaurantsApiService.getRestaurantById(orderModel.getRestaurant())
                        .doOnError(error -> Log.e(LogTag, "Can'not get restaurant with id " + orderModel.getRestaurant(), error))
                        .map(restaurant -> new FullOrder(fullOrder, restaurant)))
                // Получаем пользователя, который сделал заказ.
                .flatMap(fullOrder -> usersApiService.getUserById(orderModel.getClient())
                        .doOnError(error ->
                                Log.e(LogTag, "Can'not get client with id " + orderModel.getClient(), error)
                        )
                        .map(client -> new FullOrder(fullOrder, client)))
                // Получаем элементы меню, которые заказал пользователь
                .flatMap(fullOrder -> {
                    if (fullOrder.getMenu() != null) {
                        return Observable
                                .just(orderModel.getMenu())
                                .flatMapIterable(listId -> listId)
                                .flatMap(id -> menuApiService.getMenuItemById(orderModel.getRestaurant(), id)
                                        .toObservable()
                                        .doOnError(error -> Log.e(LogTag, "Can'not get dish with id " + id, error))
                                        .onErrorResumeNext(x -> Observable.empty())
                                        .flatMap(menuItem -> dishesApiService.getDishById(menuItem.getDishId())
                                                .toObservable()
                                                .doOnError(error -> Log.e(LogTag, "Can'not get dish with id " + menuItem.getDishId(), error))
                                                .onErrorResumeNext(x -> Observable.empty())
                                                .map(dish -> new FullMenuItem(menuItem, dish))))
                                .toList()
                                .toObservable()
                                .map(listFullMenu -> new FullOrder(fullOrder, listFullMenu))
                                .single(fullOrder);
                    } else {
                        return Single.just(fullOrder);
                    }
                })
                .doOnError(error -> Log.e(LogTag, "Can'not get order menu", error))
                // Получаем поворов, которые исполняют заказ
                .flatMap(fullOrder ->
                        Observable
                                .just(orderModel.getCooks())
                                .flatMapIterable(cooksIds -> cooksIds)
                                .flatMap(cookId -> usersApiService.getUserById(cookId)
                                        .toObservable()
                                        .doOnError(error -> Log.e(LogTag, "Can'not get cook with id " + cookId, error))
                                        .onErrorResumeNext(x -> Observable.empty())
                                )
                                .toList()
                                .toObservable()
                                .map(cooks -> new FullOrder(fullOrder, cooks, true))
                                .single(fullOrder))
                // Получаем официантов, которые исполняют заказ
                .flatMap(fullOrder ->
                        Observable.just(orderModel.getWaiters())
                                .flatMapIterable(waitersIds -> waitersIds)
                                .flatMap(waiterId -> usersApiService.getUserById(waiterId)
                                        .toObservable()
                                        .doOnError(error -> Log.e(LogTag, "Can'not get waiter with id " + waiterId, error))
                                        .onErrorResumeNext(x -> Observable.empty())
                                )
                                .toList()
                                .toObservable()
                                .map(waiters -> new FullOrder(fullOrder, waiters, false))
                                .single(fullOrder))
                // Получаем первый столик, которые забронирован в заказе
                .flatMap(fullOrder -> tablesApiService.getMenuItemById(orderModel.getRestaurant(), orderModel.getReservedTable().get(0))
                        .doOnError(error -> Log.e(LogTag, "Can'not get table with id " + orderModel.getReservedTable().get(0), error))
                        .onErrorReturn(x -> null)
                        .map(table -> new FullOrder(fullOrder, table)))
                ;
    }

    public Single<OrderModel> takeToWork(FullOrder order) {
        OrderPathModel newInfo = new OrderPathModel(OrderStatus.Preparing,
                StaffStatus.Notified,
                StaffStatus.Notified,
                null,
                null);

        return this.ordersApiService.pathOrder(order.getId(), newInfo);
    }

    public Single<OrderModel> prepareOrder(FullOrder order) {
        OrderPathModel newInfo = new OrderPathModel(OrderStatus.Prepared,
                StaffStatus.Ready,
                StaffStatus.Ready,
                null,
                null);

        return this.ordersApiService.pathOrder(order.getId(), newInfo);
    }

    public Single<OrderModel> completeOrder(FullOrder order) {
        OrderPathModel newInfo = new OrderPathModel(OrderStatus.Completed,
                null,
                null,
                null,
                null);

        return this.ordersApiService.pathOrder(order.getId(), newInfo);
    }
}
