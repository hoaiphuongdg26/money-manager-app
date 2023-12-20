package com.example.proj_moneymanager.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proj_moneymanager.R;

import java.util.ArrayList;
import java.util.List;

public class Animation_Activity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        mapping();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Fragment_Home());
        fragmentList.add(new Fragment_Setting());

        viewPager.setAdapter(new FragmentAdapter(this, fragmentList));
    }

    private void mapping() {
        viewPager = findViewById(R.id.viewPager);
    }
    public void goToHome() {
        if (viewPager.getCurrentItem() == 1) { // Kiểm tra nếu đang ở trang Setting
            viewPager.setCurrentItem(0); // Chuyển về trang Home
        }
    }
}
