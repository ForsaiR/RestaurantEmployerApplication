package com.example.restaurantemployerapplication.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.ui.list_orders.OrdersFragment;
import com.example.restaurantemployerapplication.ui.list_orders.OrdersViewModel;
import com.example.restaurantemployerapplication.ui.order.ViewOrderFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private OrdersViewModel ordersListViewModel;
    private FloatingActionButton menuFabButton;
    private BottomAppBar bottomAppBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        menuFabButton.setImageResource(R.drawable.ic_refresh_24dp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuFabButton = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bar);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        ordersListViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        initNavigation();
        initFabButton();

    }

    private void initNavigation() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        OrdersFragment ordersFragment = OrdersFragment.newInstance();
        transaction.replace(R.id.fragment_container, ordersFragment, OrdersFragment.TAG);
        transaction.commit();

        ordersListViewModel.getSelectedOrder().observe(this, order -> {
            if (order == null) {
                return;
            }

            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            menuFabButton.setImageResource(R.drawable.ic_keyboard_backspace_24dp);

            FragmentTransaction viewOrderTransaction = getSupportFragmentManager().beginTransaction();
            ViewOrderFragment viewOrderFragment = ViewOrderFragment.newInstance();
            viewOrderTransaction.replace(R.id.fragment_container, viewOrderFragment);
            viewOrderTransaction.addToBackStack(null);
            viewOrderTransaction.commit();
        });
    }

    private void initFabButton() {
        menuFabButton.setOnClickListener(view -> {
            switch (bottomAppBar.getFabAlignmentMode()) {
                case BottomAppBar.FAB_ALIGNMENT_MODE_END:
                    this.onBackPressed();
                    break;
                case BottomAppBar.FAB_ALIGNMENT_MODE_CENTER:
                    ordersListViewModel.refreshOrders();
                    break;
            }
        });

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
