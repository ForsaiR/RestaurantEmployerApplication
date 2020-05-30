package com.example.restaurantemployerapplication.ui.order;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.ui.list_orders.OrdersFragment;

public class ViewOrderFragment extends Fragment {

    public static ViewOrderFragment newInstance() {
        return new ViewOrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_order_fragment, container, false);
    }
}
