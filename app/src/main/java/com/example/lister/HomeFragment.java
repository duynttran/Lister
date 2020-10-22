package com.example.lister;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "HomeFragment - onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycle", "HomeFragment - onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Populate list of lists
        List<String> listOfListsArr = new ArrayList<>();
        listOfListsArr.add("Test List");
        listOfListsArr.add("Test List 2");
        listOfListsArr.add("Test List 3");
        ListView listOfListsView = view.findViewById(R.id.listOfLists);
        if(getContext() != null) {
            listOfListsView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listOfListsArr));
            setListViewListener(listOfListsView, listOfListsArr);
        }

        // Set up create list button
        Button createListButton = view.findViewById(R.id.newListButton);
        createListButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v){
        if(v == v.findViewById(R.id.newListButton)) {
            Log.d("Lifecycle", "HomeFragment - clicked newListButton");
            Navigation.findNavController(v).navigate(R.id.newList);
        } else {
            Log.d("Lifecycle", "HomeFragment - onClick");
            //Clicked on an already created list or maybe settings button or something.
        }
    }

    private void setListViewListener(final ListView listOfListsView, final List<String> listOfListsArr){
        listOfListsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(getContext() != null) {
                    final Dialog editListOfList = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
                    Objects.requireNonNull(editListOfList.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                    editListOfList.setContentView(R.layout.edit_list_of_list);
                    editListOfList.setCancelable(true);
                    editListOfList.show();
                }
                return true;
            }
        });
        listOfListsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Launch list fragment
                Navigation.findNavController(view).navigate(R.id.newList);
            }
        });
    }
}