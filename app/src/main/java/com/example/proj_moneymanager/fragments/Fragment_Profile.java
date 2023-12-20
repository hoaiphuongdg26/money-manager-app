package com.example.proj_moneymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.R;

public class Fragment_Profile extends Fragment {
    public Fragment_Profile(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        //Xử lý sự kiện vuốt màn hình

        return view;
    }
}