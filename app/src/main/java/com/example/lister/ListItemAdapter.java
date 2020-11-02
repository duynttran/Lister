package com.example.lister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * ListItemAdapter used for handling custom listView list items
 */
public class ListItemAdapter extends ArrayAdapter<ListItem> {
    private Context mContext;
    int mResource;

    /**
     * Default constructor for ListItemAdapter
     */
    public ListItemAdapter(Context context, int resource, List<ListItem> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Get list item information
        String name = Objects.requireNonNull(getItem(position)).getName();
        int quantity = Objects.requireNonNull(getItem(position)).getQuantity();
        double price = Objects.requireNonNull(getItem(position)).getPrice();

        //Create the list item object with the information
        ListItem listItem = new ListItem(name, quantity, price);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        //Get custom listView elements
        EditText editName = (EditText) convertView.findViewById(R.id.item_name);
        Spinner spinnerQuantity = (Spinner) convertView.findViewById(R.id.item_count);
        EditText editPrice = (EditText) convertView.findViewById(R.id.item_price);

        //Set item information
        editName.setText(name);
        spinnerQuantity.setSelection(quantity);
        editPrice.setText(String.valueOf(price));

        return convertView;
    }
}
