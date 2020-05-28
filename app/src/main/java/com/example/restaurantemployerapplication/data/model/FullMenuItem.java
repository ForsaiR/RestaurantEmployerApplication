package com.example.restaurantemployerapplication.data.model;

import com.tamagotchi.tamagotchiserverprotocol.models.DishModel;
import com.tamagotchi.tamagotchiserverprotocol.models.MenuItem;

import java.util.Objects;

/**
 * Определяем полную сущность элемента меню (информация о меню и информация о блюде)
 */
public class FullMenuItem {
    private int id;

    private int price;

    private boolean isDeleted;

    private DishModel dish;

    public FullMenuItem(int id, int price, boolean isDeleted, DishModel dish) {
        this.id = id;
        this.price = price;
        this.isDeleted = isDeleted;
        this.dish = dish;
    }

    public FullMenuItem(MenuItem menuItem, DishModel dish) {
        this.id = menuItem.getId();
        this.price = menuItem.getPrice();
        this.isDeleted = menuItem.isDeleted();
        this.dish = dish;
    }


    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public DishModel getDish() {
        return dish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullMenuItem that = (FullMenuItem) o;
        return id == that.id &&
                price == that.price &&
                isDeleted == that.isDeleted &&
                Objects.equals(dish, that.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, isDeleted, dish);
    }
}
