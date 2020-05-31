package com.example.restaurantemployerapplication.ui.order;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.ColorStateList;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.data.model.FullMenuItem;
import com.example.restaurantemployerapplication.data.model.FullVisitTime;
import com.example.restaurantemployerapplication.services.OrderStatusToStringConverter;
import com.example.restaurantemployerapplication.services.RfcToCalendarConverter;
import com.example.restaurantemployerapplication.ui.list_orders.OrdersViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.RestaurantModel;
import com.tamagotchi.tamagotchiserverprotocol.models.TableModel;
import com.tamagotchi.tamagotchiserverprotocol.models.UserModel;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.OrderStatus;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewOrderFragment extends Fragment {

    public static ViewOrderFragment newInstance() {
        return new ViewOrderFragment();
    }

    private String TAG = "ViewOrderFragment";

    private ViewOrderViewModel viewOrderViewModel;

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");

    private OrderStatusToStringConverter orderStatusToStringConverter;

    // View
    private TextView orderIdTV;
    private TextView orderLoadingErrorTV;
    private ProgressBar shortOrderLoadingProgressBar;
    private LinearLayout orderInfoContainer;
    private LinearLayout orderPaymentAmountContainer;
    private LinearLayout orderMenuContainer;
    private TextView orderPaymentAmountTV;
    private TextView personsCountTV;
    private TextView orderStatusTV;
    private TextView visitTimeStartTV;
    private TextView visitTimeEndTV;
    private TextView orderPersonTV;
    private TextView restaurantAddressTV;
    private TextView orderTableTV;
    private ListView orderMenuLV;
    private Button orderWorkButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_order_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OrdersViewModel ordersViewModel = new ViewModelProvider(getActivity()).get(OrdersViewModel.class);
        OrderModel currentOrder = ordersViewModel.getSelectedOrder().getValue();
        if (currentOrder == null) {
            throw new RuntimeException("Selected order can't be a null");
        }

        orderStatusToStringConverter = new OrderStatusToStringConverter(getActivity());

        Bundle bundle = new Bundle();
        bundle.putInt(ViewOrderViewModelFactory.ORDER_ID_TAG, currentOrder.getId());
        viewOrderViewModel = new ViewModelProvider(this,
                new ViewOrderViewModelFactory(this, bundle))
                .get(ViewOrderViewModel.class);

        initView();
        initSubscribe();
    }

    private void initView() {
        View orderV = getView();
        if (orderV == null)
            throw new RuntimeException();


        // Ошибки и загрузка
        orderLoadingErrorTV = orderV.findViewById(R.id.order_loading_error_tv);
        shortOrderLoadingProgressBar = orderV.findViewById(R.id.short_order_loading_progress_bar);

        // Контейнеры
        orderInfoContainer = orderV.findViewById(R.id.order_info_container);
        orderPaymentAmountContainer = orderV.findViewById(R.id.order_payment_amount_container);
        orderMenuContainer = orderV.findViewById(R.id.order_menu_container);

        // Кнопки
        orderWorkButton = orderV.findViewById(R.id.order_work_button);
        initOrderWorkButtonClickHandler();

        // Поля заказа
        orderIdTV = orderV.findViewById(R.id.orderId);
        orderPaymentAmountTV = orderV.findViewById(R.id.order_payment_amount);
        personsCountTV = orderV.findViewById(R.id.persons_count);
        orderStatusTV = orderV.findViewById(R.id.order_status);
        visitTimeStartTV = orderV.findViewById(R.id.visit_time_start);
        visitTimeEndTV = orderV.findViewById(R.id.visit_time_end);
        orderPersonTV = orderV.findViewById(R.id.order_person);
        restaurantAddressTV = orderV.findViewById(R.id.restaurant_address);
        orderTableTV = orderV.findViewById(R.id.order_table);
        orderMenuLV = orderV.findViewById(R.id.order_menu_list_view);

        // Устанавливаем элементы на загрузку
        orderPersonTV.setText(R.string.loading_text);
        restaurantAddressTV.setText(R.string.loading_text);
        orderTableTV.setText(R.string.loading_text);

        startLoadingAnimation(orderPersonTV);
        startLoadingAnimation(restaurantAddressTV);
        startLoadingAnimation(orderTableTV);
    }

    private void initOrderWorkButtonClickHandler() {
        orderWorkButton.setOnClickListener(view -> {
            switch (viewOrderViewModel.getOrderStatusObservable().getValue()) {
                case Confirmed:
                case Preparing:
                    if (viewOrderViewModel.getStaffStatusObservable().getValue() == StaffStatus.Notified) {
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle("Заказ подготовлен?")
                                .setMessage("Заказ подготовлен и вы желаете оповистить пользователя?")
                                .setNegativeButton("Отменить", (dialogInterface, i) -> {
                                })
                                .setPositiveButton("Подготовлен", (dialogInterface, i) -> {
                                    viewOrderViewModel.prepareOrder();
                                })
                                .show();
                    } else {
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle("Взять заказ в работу?")
                                .setMessage("Вы хотите взять в заказ в работу и начать его исполнение?")
                                .setNegativeButton("Отменить", (dialogInterface, i) -> {
                                })
                                .setPositiveButton("Взять в работу", (dialogInterface, i) -> {
                                    viewOrderViewModel.takeOrderToWork();
                                })
                                .show();
                    }
                    break;
                case Prepared:
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Завершить заказ?")
                            .setMessage("Пользователь посетил ресторан и был обслужен.")
                            .setNegativeButton("Отменить", (dialogInterface, i) -> {
                            })
                            .setPositiveButton("Завершить", (dialogInterface, i) -> {
                                viewOrderViewModel.completeOrder();
                            })
                            .show();
                    break;

            }
        });
    }

    private void startLoadingAnimation(TextView textView) {
        AlphaAnimation animation1 = new AlphaAnimation(1f, 0.2f);
        animation1.setDuration(500);
        animation1.setRepeatCount(Integer.MAX_VALUE);
        textView.startAnimation(animation1);
    }

    private void initSubscribe() {
        // Инициализурем ошибки и успешность загрузки данных.
        this.viewOrderViewModel.getIsShortInfoLoadedObservable().observe(getViewLifecycleOwner(), this::handleShortInfoLoaded);
        this.viewOrderViewModel.getIsShortInfoErrorObservable().observe(getViewLifecycleOwner(), this::handleShortInfoError);
        this.viewOrderViewModel.getIsFullInfoLoadedObservable().observe(getViewLifecycleOwner(), this::handleFullInfoLoaded);
        this.viewOrderViewModel.getIsFullInfoErrorObservable().observe(getViewLifecycleOwner(), this::handleFullInfoError);

        // Инициализируем поля заказа
        this.viewOrderViewModel.getOrderIdObservable().observe(getViewLifecycleOwner(), this::handleOrderId);
        this.viewOrderViewModel.getAmountObservable().observe(getViewLifecycleOwner(), this::handlePayment);
        this.viewOrderViewModel.getNumberPersonsObservable().observe(getViewLifecycleOwner(), this::handleNumberPersons);
        this.viewOrderViewModel.getOrderStatusObservable().observe(getViewLifecycleOwner(), this::handleOrderStatus);
        this.viewOrderViewModel.getVisitTimeObservable().observe(getViewLifecycleOwner(), this::handleVisitTime);
        this.viewOrderViewModel.getTableObservable().observe(getViewLifecycleOwner(), this::handleTable);
        this.viewOrderViewModel.getClientObservable().observe(getViewLifecycleOwner(), this::handleClient);
        this.viewOrderViewModel.getRestaurantObservable().observe(getViewLifecycleOwner(), this::handleRestaurant);
        this.viewOrderViewModel.getOrderMenuObservable().observe(getViewLifecycleOwner(), this::handleOrderMenu);

        // Инициализируем наличине меню в заказе
        this.viewOrderViewModel.getIsOrderWithMenuObservable().observe(getViewLifecycleOwner(), this::handleIsOrderWithMenu);
    }

    private void handleOrderMenu(List<FullMenuItem> menuItems) {
        if (menuItems != null) {
            OrderMenuAdapterListView orderMenuAdapterListView =
                    new OrderMenuAdapterListView(this.getContext(), new ArrayList<>(menuItems));
            this.orderMenuLV.setAdapter(orderMenuAdapterListView);
        }

    }

    private void handleRestaurant(RestaurantModel restaurant) {
        if (restaurant != null) {
            restaurantAddressTV.setText(String.format("%s", restaurant.getAddress()));
        } else {
            restaurantAddressTV.setText(R.string.unknown_text);
        }
    }

    private void handleClient(UserModel client) {
        if (client != null) {
            if (client.getFullName() != null && !client.getFullName().isEmpty()) {
                orderPersonTV.setText(String.format("%s (%s)", client.getFullName(), client.getLogin()));
            } else {
                orderPersonTV.setText(String.format("%s", client.getLogin()));
            }
        } else {
            orderPersonTV.setText(R.string.unknown_text);
        }
    }

    private void handleTable(TableModel table) {
        if (table != null) {
            orderTableTV.setText(table.getName());
        } else {
            orderTableTV.setText(R.string.unknown_text);
        }
    }

    private void handleVisitTime(FullVisitTime fullVisitTime) {
        // Устанавливаем начальное время
        String timeStartString = null;

        try {
            Calendar startTime = fullVisitTime.getStart();
            timeStartString = timeFormat.format(startTime.getTime());
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Can't convert start visit time", ex);
        }

        if (timeStartString != null) {
            visitTimeStartTV.setText(timeStartString);
        } else {
            visitTimeStartTV.setText(R.string.unknown_text);
        }

        // Устанавливаем конечное время
        String timeEndString = null;

        try {
            Calendar endTime = fullVisitTime.getEnd();
            timeEndString = timeFormat.format(endTime.getTime());
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Can't convert end visit time", ex);
        }

        if (timeEndString != null) {
            visitTimeEndTV.setText(timeEndString);
        } else {
            visitTimeEndTV.setText(R.string.unknown_text);
        }
    }

    private void handleOrderStatus(OrderStatus status) {
        if (status != null) {
            // Устанавливаем текст статуса
            String orderStatusString = orderStatusToStringConverter.convert(status);

            StaffStatus staffStatus = viewOrderViewModel.getStaffStatusObservable().getValue();
            if (staffStatus == StaffStatus.Notified) {
                orderStatusString += " (" + getContext().getResources().getString(R.string.staff_notified_status) + ")";
            }

            orderStatusTV.setText(orderStatusString);

            // Обновляем текст кнопки, либо скрываем ее
            if (status == OrderStatus.Prepared) {
                orderWorkButton.setText(R.string.complete_the_order);
                orderWorkButton.setVisibility(View.VISIBLE);
            } else if ((status == OrderStatus.Preparing && staffStatus == StaffStatus.Notifying) || status == OrderStatus.Confirmed) {
                orderWorkButton.setText(R.string.take_order_to_work);
                orderWorkButton.setVisibility(View.VISIBLE);
            } else if (status == OrderStatus.Preparing || staffStatus == StaffStatus.Notified) {
                orderWorkButton.setText(R.string.notify_client_about_oder_ready);
                orderWorkButton.setVisibility(View.VISIBLE);
            } else {
                orderWorkButton.setVisibility(View.GONE);
            }
        } else {
            orderStatusTV.setText(R.string.unknown_text);
        }
    }

    private void handleNumberPersons(Integer numberPersons) {
        if (numberPersons != null) {
            String stringPersons = String.format(Locale.getDefault(), "%d", numberPersons);
            personsCountTV.setText(stringPersons);
        } else {
            personsCountTV.setText(R.string.unknown_text);
        }
    }

    private void handlePayment(Integer amount) {
        if (amount != null) {
            String stringAmount = String.format(Locale.getDefault(), "%d", amount);
            orderPaymentAmountTV.setText(stringAmount);
        } else {
            orderPaymentAmountTV.setText(R.string.unknown_text);
        }
    }

    private void handleOrderId(Integer orderId) {
        if (orderId != null) {
            String stringId = String.format(Locale.getDefault(), "%d", orderId);
            orderIdTV.setText(stringId);
        } else {
            orderIdTV.setText(R.string.unknown_text);
        }
    }

    private void handleIsOrderWithMenu(Boolean isWithMenu) {
        if (isWithMenu) {
            orderPaymentAmountContainer.setVisibility(View.VISIBLE);
            orderMenuContainer.setVisibility(View.VISIBLE);
        } else {
            orderPaymentAmountContainer.setVisibility(View.GONE);
            orderMenuContainer.setVisibility(View.GONE);
        }
    }

    private void handleFullInfoError(Boolean isError) {
        if (isError) {
            shortOrderLoadingProgressBar.setIndeterminate(false);
            shortOrderLoadingProgressBar.setProgress(100);

            ColorStateList colorStateList = ContextCompat.getColorStateList(getActivity(), R.color.design_default_color_error);
            shortOrderLoadingProgressBar.setProgressTintList(colorStateList);
        }
    }

    private void handleFullInfoLoaded(Boolean isLoaded) {
        if (isLoaded) {
            shortOrderLoadingProgressBar.setVisibility(View.INVISIBLE);
            orderPersonTV.clearAnimation();
            restaurantAddressTV.clearAnimation();
            orderTableTV.clearAnimation();
        }
    }

    private void handleShortInfoError(Boolean isError) {
        if (isError) {
            shortOrderLoadingProgressBar.setIndeterminate(false);
            shortOrderLoadingProgressBar.setProgress(100);

            ColorStateList colorStateList = ContextCompat.getColorStateList(getActivity(), R.color.design_default_color_error);
            shortOrderLoadingProgressBar.setProgressTintList(colorStateList);

            orderLoadingErrorTV.setVisibility(View.VISIBLE);
        }
    }

    private void handleShortInfoLoaded(Boolean isLoaded) {
        if (isLoaded) {
            orderInfoContainer.setVisibility(View.VISIBLE);
        } else {
            shortOrderLoadingProgressBar.setVisibility(View.VISIBLE);
            orderInfoContainer.setVisibility(View.GONE);
        }
    }
}
