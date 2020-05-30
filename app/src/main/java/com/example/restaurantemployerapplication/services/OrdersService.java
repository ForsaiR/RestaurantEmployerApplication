package com.example.restaurantemployerapplication.services;

import android.util.Log;

import com.example.restaurantemployerapplication.data.model.FullMenuItem;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderPathModel;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;
import com.tamagotchi.tamagotchiserverprotocol.routers.IDishesApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IMenuApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IOrdersApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IRestaurantsApiService;
import com.tamagotchi.tamagotchiserverprotocol.routers.IUsersApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
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
    private IUsersApiService usersApiService;

    @Inject
    OrdersService(IOrdersApiService ordersApiService,
                  IMenuApiService menuApiService,
                  IDishesApiService dishesApiService,
                  IRestaurantsApiService restaurantsApiService,
                  IUsersApiService usersApiService) {
        this.ordersApiService = ordersApiService;
        this.menuApiService = menuApiService;
        this.dishesApiService = dishesApiService;
        this.restaurantsApiService = restaurantsApiService;
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

//        return ordersApiService.
//                getAllOrders(null, OrderStatus.Confirmed, null, null)
//                .mergeWith(
//                        ordersApiService.getAllOrders(null, OrderStatus.Preparing, null, null)
//                )
//                .mergeWith(
//                        ordersApiService.getAllOrders(null, OrderStatus.Prepared, null, null)
//                )
//                .mergeWith(
//                        ordersApiService.getAllOrders(null, OrderStatus.PaymentMadeing, null, null)
//                ).toObservable().single(new ArrayList<>());
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
     * Получить активные заказы системы.
     * TODO: присутсвует серьезный баг: если состояние было изменено на такое же,
     * которое приходило до этого, то эмита элемента не будет. Исправление займет много времени,
     * пока ситуации, когда такое может быть в автоматическом режиме, не существует.
     * TODO: при ошибке загрузки зависимых сущностей целевой элемент (FullOrder) не будет испускаться.
     * Ожидаемый результат: должен испускаться с null в зависимом элементе.
     *
     * @return observable на заказы со статусом {@link OrderStatus#Preparing}.
     */
    public Observable<FullOrder> activeOrders() {
        long firstEmit = 0;
        return Observable.interval(updateInterval, TimeUnit.SECONDS, Schedulers.io())
                .startWithItem(firstEmit)
                .flatMap(time -> ordersApiService.getAllOrders(null,
                        null,
                        null,
                        null).toObservable())
                .flatMapIterable(itemOrders -> itemOrders)
                .distinct()
                .flatMap(order ->
                        // Получаем элементы меню, которые заказал пользователь
                        Observable.just(order.getMenu())
                                .flatMapIterable(listId -> listId)
                                .flatMap(id -> menuApiService.getMenuItemById(order.getRestaurant(), id).toObservable()
                                        .doOnError(x -> Log.e(LogTag, "Can'not find dish with id " + id))
                                        .onErrorResumeNext(
                                                x -> {
                                                    Log.e(LogTag, x.toString());
                                                    return Observable.empty();
                                                })
                                        .flatMap(menuItem -> dishesApiService.getDishById(menuItem.getDishId())
                                                .toObservable()
                                                .doOnError(x -> Log.e(LogTag, "Can'not find dish with id " + menuItem.getDishId()))
                                                .onErrorResumeNext(
                                                        x -> {
                                                            return Observable.empty();
                                                        })
                                                .map(dish -> new FullMenuItem(menuItem, dish))))
                                .toList()
                                .toObservable()
                                .map(listFullMenu -> new FullOrder(order, listFullMenu))
                                .flatMap(fullOrder -> restaurantsApiService.getRestaurantById(order.getRestaurant())
                                        .doOnError(x -> Log.e(LogTag, "Can'not find restaurant with id " + order.getRestaurant()))
                                        .map(restaurant -> new FullOrder(fullOrder, restaurant)).toObservable())
                                .flatMap(fullOrder -> usersApiService.getUserById(order.getClient())
                                        .doOnError(x -> Log.e(LogTag, "Can'not find user with id " + order.getClient()))
                                        .map(client -> new FullOrder(fullOrder, client)).toObservable())
                );
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
