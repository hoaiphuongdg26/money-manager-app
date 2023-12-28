package com.example.proj_moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.activities.Setting.Setting;
import com.example.proj_moneymanager.databinding.FragmentHomeBinding;
public class HomeFragment extends Fragment {
    private FragmentHomeBinding homeBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            // Bắt sự kiện click vào button "setting" sau khi view đã được tạo
            homeBinding.buttonSetting.setOnClickListener(v -> {
            // Hiển thị toast message khi nút được click
            Toast.makeText(requireContext(), "Button Setting clicked!", Toast.LENGTH_SHORT).show();
            //Chuyển qua setting
                // Tạo Intent để chuyển từ Fragment này sang Activity Setting
                Intent intent = new Intent(requireActivity(), Setting.class);
                // Bắt đầu Activity mới
                startActivity(intent);
            });
    }
}