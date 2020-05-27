package com.example.restaurantemployerapplication.data.model;

import com.example.restaurantemployerapplication.services.RfcToCalendarConverter;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.RestaurantModel;
import com.tamagotchi.tamagotchiserverprotocol.models.UserModel;
import com.tamagotchi.tamagotchiserverprotocol.models.VisitTime;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FullOrder {
    private RestaurantModel restaurant;
    private UserModel client;
    private List<FullMenuItem> menu;
    private Integer numberOfPersons;
    private String comment;
    private OrderStatus orderStatus;
    private StaffStatus orderCooksStatus;
    private StaffStatus orderWaitersStatus;
    private List<Integer> cooks;
    private List<Integer> waiters;
    private Integer totalAmount;
    private Integer id;
    private FullVisitTime visitTime;
    private Calendar timeCreated;

    public FullOrder(RestaurantModel restaurant,
                     UserModel client,
                     List<FullMenuItem> menu,
                     Integer numberOfPersons,
                     String comment,
                     OrderStatus orderStatus,
                     StaffStatus orderCooksStatus,
                     StaffStatus orderWaitersStatus,
                     List<Integer> cooks,
                     List<Integer> waiters,
                     Integer totalAmount,
                     Integer id,
                     FullVisitTime visitTime,
                     Calendar timeCreated) {
        this.restaurant = restaurant;
        this.client = client;
        this.menu = menu;
        this.numberOfPersons = numberOfPersons;
        this.comment = comment;
        this.orderStatus = orderStatus;
        this.orderCooksStatus = orderCooksStatus;
        this.orderWaitersStatus = orderWaitersStatus;
        this.cooks = cooks;
        this.waiters = waiters;
        this.totalAmount = totalAmount;
        this.id = id;
        this.visitTime = visitTime;
        this.timeCreated = timeCreated;
    }

    public FullOrder(OrderModel orderModel, List<FullMenuItem> menuItems) {
        this.restaurant = null;
        this.client = null;
        this.menu = menuItems;
        this.numberOfPersons = orderModel.getNumberOfPersons();
        this.comment = orderModel.getComment();
        this.orderStatus = orderModel.getOrderStatus();
        this.orderCooksStatus = orderModel.getOrderCooksStatus();
        this.orderWaitersStatus = orderModel.getOrderWaitersStatus();
        this.cooks = orderModel.getCooks();
        this.waiters = orderModel.getWaiters();
        this.totalAmount = orderModel.getTotalAmount();
        this.id = orderModel.getId();
        this.visitTime = new FullVisitTime(orderModel.getVisitTime());
        this.timeCreated = RfcToCalendarConverter.convert(orderModel.getTimeCreated());
    }

    public FullOrder(FullOrder order, RestaurantModel restaurant) {
        this.restaurant = restaurant;
        this.client = order.getClient();
        this.menu = order.getMenu();
        this.numberOfPersons = order.getNumberOfPersons();
        this.comment = order.getComment();
        this.orderStatus = order.getOrderStatus();
        this.orderCooksStatus = order.getOrderCooksStatus();
        this.orderWaitersStatus = order.getOrderWaitersStatus();
        this.cooks = order.getCooks();
        this.waiters = order.getWaiters();
        this.totalAmount = order.getTotalAmount();
        this.id = order.getId();
        this.visitTime = order.getVisitTime();
        this.timeCreated = order.getTimeCreated();
    }

    public FullOrder(FullOrder order, UserModel client) {
        this.restaurant = order.getRestaurant();
        this.client = client;
        this.menu = order.getMenu();
        this.numberOfPersons = order.getNumberOfPersons();
        this.comment = order.getComment();
        this.orderStatus = order.getOrderStatus();
        this.orderCooksStatus = order.getOrderCooksStatus();
        this.orderWaitersStatus = order.getOrderWaitersStatus();
        this.cooks = order.getCooks();
        this.waiters = order.getWaiters();
        this.totalAmount = order.getTotalAmount();
        this.id = order.getId();
        this.visitTime = order.getVisitTime();
        this.timeCreated = order.getTimeCreated();
    }

    public RestaurantModel getRestaurant() {
        return restaurant;
    }

    public UserModel getClient() {
        return client;
    }

    public List<FullMenuItem> getMenu() {
        return menu;
    }

    public Integer getNumberOfPersons() {
        return numberOfPersons;
    }

    public String getComment() {
        return comment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public StaffStatus getOrderCooksStatus() {
        return orderCooksStatus;
    }

    public StaffStatus getOrderWaitersStatus() {
        return orderWaitersStatus;
    }

    public List<Integer> getCooks() {
        return cooks;
    }

    public List<Integer> getWaiters() {
        return waiters;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public Integer getId() {
        return id;
    }

    public FullVisitTime getVisitTime() {
        return visitTime;
    }

    public Calendar getTimeCreated() {
        return timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullOrder fullOrder = (FullOrder) o;
        return Objects.equals(restaurant, fullOrder.restaurant) &&
                Objects.equals(client, fullOrder.client) &&
                Objects.equals(menu, fullOrder.menu) &&
                Objects.equals(numberOfPersons, fullOrder.numberOfPersons) &&
                Objects.equals(comment, fullOrder.comment) &&
                orderStatus == fullOrder.orderStatus &&
                orderCooksStatus == fullOrder.orderCooksStatus &&
                orderWaitersStatus == fullOrder.orderWaitersStatus &&
                Objects.equals(cooks, fullOrder.cooks) &&
                Objects.equals(waiters, fullOrder.waiters) &&
                Objects.equals(totalAmount, fullOrder.totalAmount) &&
                Objects.equals(id, fullOrder.id) &&
                Objects.equals(visitTime, fullOrder.visitTime) &&
                Objects.equals(timeCreated, fullOrder.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurant, client, menu, numberOfPersons, comment, orderStatus, orderCooksStatus, orderWaitersStatus, cooks, waiters, totalAmount, id, visitTime, timeCreated);
    }
}
