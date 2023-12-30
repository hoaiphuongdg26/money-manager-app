package com.example.proj_moneymanager;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proj_moneymanager.Object.UserInformation;
import com.example.proj_moneymanager.activities.ExpenseFragment;
import com.example.proj_moneymanager.activities.HomeFragment;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.activities.Profile.ProfileFragment;
import com.example.proj_moneymanager.activities.Statistic.StatisticFragment;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BroadcastReceiver broadcastReceiver;
    DbHelper database = Login.database;
    long UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        // Lấy dữ liệu từ Login trong intent
        Intent intent = getIntent();
        UserInformation userInformation = new UserInformation();
        UserID = intent.getIntExtra("UserID", 0);

        // Gửi dữ liệu đến ExpenseFragment
        Bundle bundle = new Bundle();
        bundle.putLong("UserID", UserID);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        StatisticFragment statisticFragment = new StatisticFragment();
        statisticFragment.setArguments(bundle);

        ExpenseFragment expenseFragment = new ExpenseFragment();
        expenseFragment.setArguments(bundle);

        CalendarFragment calendarFragment = new CalendarFragment();
        calendarFragment.setArguments(bundle);

        ProfileFragment profileFragment = new ProfileFragment(this);
        profileFragment.setArguments(bundle);

        binding.navBar.setOnItemSelectedListener(item->{
            switch (item.getItemId()) {
                case R.id.menu_home:
                    replaceFragment(homeFragment);
                    binding.textviewHeader.setText("HOME");
                    break;
                case R.id.menu_chart:
                    replaceFragment(statisticFragment);
                    binding.textviewHeader.setText("STATISTC");
                    break;
                case R.id.menu_money:
                    replaceFragment(expenseFragment);
                    binding.textviewHeader.setText("EXPENSE");
                    break;
                case R.id.menu_calendar:
                    replaceFragment(calendarFragment);
                    binding.textviewHeader.setText("CALENDAR");
                    break;
                case R.id.menu_profile:
                    replaceFragment(profileFragment);
                    binding.textviewHeader.setText("PROFILE");
                    break;
            }
            return true;
        });

        Toast.makeText(getApplicationContext(), "Welcome, "+ UserInformation.getFullName(getBaseContext(), UserID), Toast.LENGTH_LONG).show();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}