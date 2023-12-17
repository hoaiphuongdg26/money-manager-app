package com.example.proj_moneymanager.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;

public class Expense extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //ẩn action bar
        setContentView(R.layout.expense);
    }
}
