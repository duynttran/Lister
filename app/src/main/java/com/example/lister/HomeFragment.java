package com.example.lister;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.navigation.Navigation;

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
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

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
}