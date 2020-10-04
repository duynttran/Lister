package com.example.lister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button createListButton = findViewById(R.id.newListButton);
        createListButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        setContentView(R.layout.activity_create_list);
    }
}