package com.example.lister;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment{

    String list;
    List<ListItem> itemsList;
    ListItemAdapter itemsAdapter;

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
        list = ListFragmentArgs.fromBundle(getArguments()).getList();

        //Disable enter button since no list name entered yet
        final Button enterButton = view.findViewById(R.id.enterButton);
        enterButton.setEnabled(false);
        enterButton.setVisibility(View.GONE);

        //Enable enter button upon opening keyboard for list name
        final TextView inputListName = view.findViewById(R.id.inputListName);
        inputListName.setText(list);
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
                inputListName.clearFocus();
            }
        });

        // Populate items list
        itemsList = new ArrayList<>();
        itemsList.add(new ListItem("Cookies", 1, 4.32));
        itemsList.add(new ListItem("Breasts", 2, 7.13));
        itemsList.add(new ListItem("Drums", 3, 9.76));
        ListView listView = view.findViewById(R.id.listOfItems);
        if(getContext() != null) {
            itemsAdapter = new ListItemAdapter(getContext(), R.layout.listview_item, itemsList);
            listView.setAdapter(itemsAdapter);
            setListViewListener(listView, itemsList);
        }

        view.findViewById(R.id.newItemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsAdapter.add(new ListItem("default", 0, 0.00));
            }
        });

        return view;
    }

    private void setListViewListener(final ListView listView, final List<ListItem> items){
        //Todo: These items will have other views in them and may actually be a custom list view.

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(getContext() != null) {
                    final Dialog editListOfList = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
                    Objects.requireNonNull(editListOfList.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                    //Todo: This should edit a list item, not a list of lists.
                    editListOfList.setContentView(R.layout.edit_list_of_list);
                    editListOfList.setCancelable(true);
                    editListOfList.show();
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Something maybe?
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }
}