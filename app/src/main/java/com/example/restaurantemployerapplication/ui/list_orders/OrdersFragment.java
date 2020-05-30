package com.example.restaurantemployerapplication.ui.list_orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.restaurantemployerapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    public static final String TAG = "ORDERS";
    private OrdersViewModel viewModel;
    private ListView ordersListView;
    private TabLayout tabs;
    OrdersFragment ordersFragment;

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.orders_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(OrdersViewModel.class);
        ordersFragment = (OrdersFragment) getFragmentManager().findFragmentByTag(TAG);
        initListView();
        initNavigationTabs();
    }

    private void initNavigationTabs() {
        tabs = this.getView().findViewById(R.id.tabs);
        TabLayout.Tab tab = tabs.getTabAt(0);
        tabs.selectTab(tab);
        ordersFragment.initActiveOrdersSubscribe();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tabs.getSelectedTabPosition();
                viewModel.getActiveOrders().removeObservers(ordersFragment.getViewLifecycleOwner());
                viewModel.getAllOrders().removeObservers(ordersFragment.getViewLifecycleOwner());
                viewModel.getCompletedOrders().removeObservers(ordersFragment.getViewLifecycleOwner());

                if (position == Tab.Active.position) {
                    ordersFragment.initActiveOrdersSubscribe();
                } else if (position == Tab.Completed.position) {
                    ordersFragment.initCompletedOrdersSubscribe();
                } else if (position == Tab.All.position) {
                    ordersFragment.initAllOrdersSubscribe();
                } else {
                    throw new RuntimeException("Not supported");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initListView() {
        ordersListView = this.getView().findViewById(R.id.orderList);

        ordersListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            ArrayAdapter<OrderModel> adapter = (ArrayAdapter<OrderModel>) parent.getAdapter();
            OrderModel order = adapter.getItem(position);
            viewModel.setSelectedOrder(order);
        });
    }

    public void initActiveOrdersSubscribe() {
        viewModel.getActiveOrders().observe(getViewLifecycleOwner(),
                this::initListViewAdapter
        );
    }

    public void initCompletedOrdersSubscribe() {
        viewModel.getCompletedOrders().observe(getViewLifecycleOwner(),
                this::initListViewAdapter
        );
        viewModel.refreshOrders();
    }

    public void initAllOrdersSubscribe() {
        viewModel.getAllOrders().observe(getViewLifecycleOwner(),
                this::initListViewAdapter
        );
        viewModel.refreshOrders();
    }

    private void initListViewAdapter(List<OrderModel> orders) {
        ordersListView.setAdapter(new OrdersAdapterListView(this.getContext(), new ArrayList<>(orders)));
    }


    private enum Tab {
        Active(0),
        Completed(1),
        All(2);

        public final Integer position;

        private Tab(Integer position) {
            this.position = position;
        }
    }
}
