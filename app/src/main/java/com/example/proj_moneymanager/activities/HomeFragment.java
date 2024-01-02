package com.example.proj_moneymanager.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Setting.SettingFragment;
import com.example.proj_moneymanager.databinding.FragmentHomeBinding;
public class HomeFragment extends Fragment {
    private FragmentHomeBinding homeBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        // Bắt sự kiện click vào button "setting" sau khi view đã được tạo
        ImageButton imagebuttonEditCategory = homeBinding.buttonSetting;
        imagebuttonEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi sự kiện khi click
                onSettingButtonClick();
            }
        });
        return homeBinding.getRoot();
    }
    public void onSettingButtonClick (){
        SettingFragment settingFragment = new SettingFragment();

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, settingFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}