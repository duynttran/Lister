package com.example.lister;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment{

    int listId;
    List<ListItem> itemsList;
    ListItemAdapter itemsAdapter;
    ListDatabaseHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "ListFragment - onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycle", "ListFragment - onCreateView");
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        assert getArguments() != null;
        listId = ListFragmentArgs.fromBundle(getArguments()).getList();
        helper = ListDatabaseHelper.getInstance(getContext());
        //Disable enter button since no list name entered yet
        final Button enterButton = view.findViewById(R.id.enterButton);
        enterButton.setEnabled(false);
        enterButton.setVisibility(View.GONE);

        //Enable enter button upon opening keyboard for list name
        final TextView inputListName = view.findViewById(R.id.inputListName);
        String listName = helper.getListName(listId);
        inputListName.setText(listName);
        inputListName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                enterButton.setEnabled(b);
                if(b){
                    enterButton.setVisibility(View.VISIBLE);
                } else {
                    enterButton.setVisibility(View.GONE);
                    hideKeyboard();
                }
            }
        });
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String newName = inputListName.getText().toString();
                helper.updateListName(newName, listId);
            }
        });

        // Populate items list
        itemsList = new ArrayList<>();
        ListView listView = view.findViewById(R.id.listOfItems);
        if(getContext() != null) {
            itemsList = helper.getListItems(listId);
            itemsAdapter = new ListItemAdapter(getActivity(), getContext(), R.layout.listview_item, itemsList);
            listView.setAdapter(itemsAdapter);
            setListViewListener(listView, itemsList);
        }
        TextView totalPriceView = view.findViewById(R.id.totalPrice);
        itemsAdapter.initializeTotalTV(totalPriceView);
        itemsAdapter.updateTotalPrice();

        // Button to add new item
        view.findViewById(R.id.newItemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsAdapter.clear();
                itemsAdapter.addAll(helper.getListItems(listId));
                itemsAdapter.notifyDataSetChanged();
                ListItem item = new ListItem("default", 0, 0.00, listId);
                int itemId = helper.addItem(item);
                item.setItemId(itemId);
                itemsAdapter.add(item);
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        for(ListItem item : itemsList) {
            item.setPrice(Double.parseDouble(helper.getItemPrice(item.getItemId())));
        }
    }
    private void setListViewListener(final ListView listView, final List<ListItem> itemsList){
        //Removes list item on long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(getContext() != null) {
                    itemsAdapter.clear();
                    itemsAdapter.addAll(helper.getListItems(listId));
                    itemsAdapter.notifyDataSetChanged();
                    ListItem removed = itemsList.remove(i);
                    itemsAdapter.remove(removed);
                    helper.deleteItem(removed.getItemId());
                }
                return true;
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }
}