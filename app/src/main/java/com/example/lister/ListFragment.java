package com.example.lister;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "ListFragment - onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycle", "ListFragment - onCreateView");
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}