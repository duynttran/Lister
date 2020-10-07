package com.example.lister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        Log.d("Lifecycle", "MainActivity - onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Lifecycle", "MainActivity - onPause");
        super.onPause();
    }
}