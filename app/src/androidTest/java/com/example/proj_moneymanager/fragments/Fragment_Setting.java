package com.example.proj_moneymanager.fragments;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.R;

public class Fragment_Setting extends Fragment {
    public Fragment_Setting(){}
    private GestureDetector gestureDetector;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting, container, false);

        return view;


    }
}