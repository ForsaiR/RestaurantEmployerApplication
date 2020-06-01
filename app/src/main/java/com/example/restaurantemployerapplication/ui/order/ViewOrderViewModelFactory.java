package com.example.restaurantemployerapplication.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import org.jetbrains.annotations.NotNull;

public class ViewOrderViewModelFactory extends AbstractSavedStateViewModelFactory {
    final static String ORDER_ID_TAG = "orderID";
    private int orderId;

    /**
     * Constructs this factory.
     *
     * @param owner       {@link SavedStateRegistryOwner} that will provide restored state for created
     *                    {@link ViewModel ViewModels}
     * @param defaultArgs values from this {@code Bundle} will be used as defaults by
     *                    {@link SavedStateHandle} passed in {@link ViewModel ViewModels}
     *                    if there is no previously saved state
     */
    public ViewOrderViewModelFactory(@NonNull SavedStateRegistryOwner owner, @NotNull Bundle defaultArgs) {
        super(owner, defaultArgs);
        this.orderId = defaultArgs.getInt(ORDER_ID_TAG);
    }

    @NonNull
    @Override
    protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
        return (T) (new ViewOrderViewModel(orderId));
    }
}
