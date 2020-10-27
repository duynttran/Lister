package com.example.lister;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    List<AbstractMap.SimpleEntry<String, Integer>> listOfListsArr;
    int editingListId = -1;
    ListDatabaseHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "HomeFragment - onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycle", "HomeFragment - onCreateView");

        //get singleton instance of database
        helper = ListDatabaseHelper.getInstance(getContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Populate list of lists
        listOfListsArr = helper.getAllLists();
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
        //get singleton instance of database
        ListDatabaseHelper helper = ListDatabaseHelper.getInstance(getContext());

        if(v == v.findViewById(R.id.newListButton)) {
            Log.d("Lifecycle", "HomeFragment - clicked newListButton");
            helper.addList("New List");
        } else {
            Log.d("Lifecycle", "HomeFragment - onClick");
            HomeFragmentDirections.EditList action = HomeFragmentDirections.editList();
            //Clicked on an already created list or maybe settings button or something.
        }

        //Refresh fragment view
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    private void setListViewListener(final ListView listOfListsView, final List<AbstractMap.SimpleEntry<String, Integer>> list){
        listOfListsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                if(getContext() != null) {
                    editingListId = list.get(i).getValue();
                    final Dialog editListOfList = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
                    Objects.requireNonNull(editListOfList.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                    editListOfList.setContentView(R.layout.edit_list_of_list);
                    editListOfList.setCancelable(true);
                    final TextView editText = editListOfList.findViewById(R.id.editListName);
                    editText.append(list.get(i).getKey());
                    editListOfList.show();
                    editListOfList.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Lifecycle", "HomeFragment - EditListOfLists - clicked deleteButton");
                            editListOfList.hide();
                            helper.deleteList(editingListId);
                        }
                    });
                    editListOfList.findViewById(R.id.enterButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Lifecycle", "HomeFragment - EditListOfLists - clicked enterButton");
                            editListOfList.hide();
                            helper.updateListName(editText.getText().toString(), editingListId);
                        }
                    });
                }
                return true;
            }
        });
        listOfListsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Launch list fragment
                HomeFragmentDirections.EditList action = HomeFragmentDirections.editList();
                action.setList(list.get(i).getKey());
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
}