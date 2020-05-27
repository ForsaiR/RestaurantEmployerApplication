package com.example.restaurantemployerapplication.ui.main;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.data.model.FullOrder;
import com.example.restaurantemployerapplication.services.OrderStatusToStringConverter;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;

import java.util.ArrayList;

public class OrdersAdapterListView extends ArrayAdapter<FullOrder> {

    private OrderStatusToStringConverter statusConverter;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
    private static final String textError = "???";

    public OrdersAdapterListView(Context context, ArrayList<FullOrder> orders) {
        super(context, R.layout.orders_item_lv, orders);
        statusConverter = new OrderStatusToStringConverter(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FullOrder order = getItem(position);
        if (order == null)
            throw new RuntimeException("Can not be null");

        FullOrderHolder viewHolder; // view lookup cache stored in tag
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new FullOrderHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.orders_item_lv, parent, false);
            viewHolder.state = convertView.findViewById(R.id.orderState);
            viewHolder.id = convertView.findViewById(R.id.orderId);
            viewHolder.time = convertView.findViewById(R.id.orderTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FullOrderHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        String orderStatusString = order.getOrderStatus() != null ?
                statusConverter.convert(order.getOrderStatus()) : textError;

        if (order.getOrderCooksStatus() == StaffStatus.Notified && order.getOrderWaitersStatus() == StaffStatus.Notified) {
            orderStatusString += " (" + getContext().getResources().getString(R.string.staff_notified_status) + ")";
        }

            viewHolder.state.setText(orderStatusString);
        viewHolder.id.setText(order.getId() != null ? order.getId().toString() : textError);

        String timeString = textError;
        try {
            timeString = timeFormat.format(order.getVisitTime().getStart().getTime());
        } catch (IllegalArgumentException ignored) {
        }

        viewHolder.time.setText(timeString);

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class FullOrderHolder {
        TextView id;
        TextView state;
        TextView time;
    }
}
