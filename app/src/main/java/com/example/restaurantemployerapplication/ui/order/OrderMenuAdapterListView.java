package com.example.restaurantemployerapplication.ui.order;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.restaurantemployerapplication.R;
import com.example.restaurantemployerapplication.data.model.FullMenuItem;
import com.example.restaurantemployerapplication.services.OrderStatusToStringConverter;
import com.example.restaurantemployerapplication.services.RfcToCalendarConverter;
import com.tamagotchi.tamagotchiserverprotocol.models.OrderModel;
import com.tamagotchi.tamagotchiserverprotocol.models.enums.StaffStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OrderMenuAdapterListView extends ArrayAdapter<FullMenuItem> {

    private String TAG = "OrderMenuAdapterListView";

    public OrderMenuAdapterListView(Context context, ArrayList<FullMenuItem> menuItems) {
        super(context, R.layout.order_menu_item_lv, menuItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FullMenuItem menuItem = getItem(position);
        if (menuItem == null)
            throw new RuntimeException("Can not be null");

        FullMenuItemHolder viewHolder; // view lookup cache stored in tag
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new FullMenuItemHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_menu_item_lv, parent, false);
            viewHolder.numberInList = convertView.findViewById(R.id.number_in_list);
            viewHolder.menuItemName = convertView.findViewById(R.id.menu_item_name);
            viewHolder.menuItemId = convertView.findViewById(R.id.menu_item_id);
            viewHolder.menuItemPrice = convertView.findViewById(R.id.menu_item_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FullMenuItemHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.numberInList.setText(String.format(Locale.getDefault(), "%d", position + 1));
        viewHolder.menuItemName.setText(menuItem.getDish().getName());
        viewHolder.menuItemId.setText(String.format(Locale.getDefault(), "%d",menuItem.getId()));
        viewHolder.menuItemPrice.setText(String.format(Locale.getDefault(), "%d",menuItem.getPrice()));

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class FullMenuItemHolder {
        TextView numberInList;
        TextView menuItemName;
        TextView menuItemId;
        TextView menuItemPrice;
    }
}
