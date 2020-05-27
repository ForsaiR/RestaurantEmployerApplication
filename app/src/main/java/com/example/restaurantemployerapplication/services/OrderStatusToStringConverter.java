package com.example.restaurantemployerapplication.services;

import android.content.Context;

import com.example.restaurantemployerapplication.R;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;

public class OrderStatusToStringConverter {
    private Context context;

    public OrderStatusToStringConverter(Context context) {
        this.context = context;
    }

    private String getStringFromResource(int id) {
        return context.getResources().getString(id);
    }

    public String convert(OrderStatus status) {
        switch (status) {
            case NoPlace:
                return getStringFromResource(R.string.no_place_status);
            case PaymentMadeing:
                return getStringFromResource(R.string.payment_madeing_status);
            case PaymentError:
                return getStringFromResource(R.string.payment_error_status);
            case Confirmed:
                return getStringFromResource(R.string.confirmed_status);
            case Preparing:
                return getStringFromResource(R.string.preparing_status);
            case Prepared:
                return getStringFromResource(R.string.prepared_status);
            case Completed:
                return getStringFromResource(R.string.completed_status);
            default:
                throw new RuntimeException("Unknown state: " + status.toString());
        }
    }
}
