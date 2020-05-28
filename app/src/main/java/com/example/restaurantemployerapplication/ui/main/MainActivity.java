package com.example.restaurantemployerapplication.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private ListView ordersListView;
    private FloatingActionButton refreshButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ordersListView = findViewById(R.id.orderList);
        refreshButton = findViewById(R.id.fab);

        initListView();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        initSubscribe();
        initRefreshButton();
    }

    private void initRefreshButton() {
        refreshButton.setOnClickListener(view -> {
            viewModel.refresh();
        });

    }

    private void initListView() {
        ordersListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            ArrayAdapter<FullOrder> adapter = (ArrayAdapter<FullOrder>) parent.getAdapter();
            FullOrder order = adapter.getItem(position);

            switch (order.getOrderStatus()) {
                case Preparing:
                    if (order.getOrderCooksStatus() == StaffStatus.Notified
                            || order.getOrderWaitersStatus() == StaffStatus.Notified) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Заказ подготовлен?")
                                .setMessage("Заказ подготовлен и вы желаете оповистить пользователя?")
                                .setNegativeButton("Отменить", (dialogInterface, i) -> {
                                })
                                .setPositiveButton("Подготовлен", (dialogInterface, i) -> {
                                    viewModel.prepareOrder(order);
                                })
                                .show();
                    } else {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Взять заказ в работу?")
                                .setMessage("Вы хотите взять в заказ в работу и начать его исполнение?")
                                .setNegativeButton("Отменить", (dialogInterface, i) -> {
                                })
                                .setPositiveButton("Взять в работу", (dialogInterface, i) -> {
                                    viewModel.takeOrderToWork(order);
                                })
                                .show();
                    }
                    break;
                case Prepared:
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Завершить заказ?")
                            .setMessage("Пользователь посетил ресторан и был обслужен.")
                            .setNegativeButton("Отменить", (dialogInterface, i) -> {
                            })
                            .setPositiveButton("Завершить", (dialogInterface, i) -> {
                                viewModel.completeOrder(order);
                            })
                            .show();
                    break;

            }

        });
    }

    private void initSubscribe() {
        viewModel.preparingOrders().observe(this, this::initListViewAdapter);
    }

    private void initListViewAdapter(List<FullOrder> orders) {
        ordersListView.setAdapter(new OrdersAdapterListView(this, new ArrayList<>(orders)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Обработка нажатия выхода из приложения.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(R.string.logOut)
                    .setMessage(R.string.logOutConfirm)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        viewModel.logOut();
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                        // Ничего не делаем
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
}
