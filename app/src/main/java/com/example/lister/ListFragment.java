package com.example.lister;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ListFragment extends Fragment{

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

        //Disable enter button since no list name entered yet
        final Button enterButton = view.findViewById(R.id.enterButton);
        enterButton.setEnabled(false);
        enterButton.setVisibility(View.GONE);

        //Enable enter button upon opening keyboard for list name
        TextView inputListName = view.findViewById(R.id.inputListName);
        inputListName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                enterButton.setEnabled(b);
                if(b){
                    enterButton.setVisibility(View.VISIBLE);
                } else {
                    enterButton.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
}