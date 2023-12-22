package com.example.proj_moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.example.proj_moneymanager.database.Database;
import com.example.proj_moneymanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Database database = Login.database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.navBar.setOnItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.menu_home:
                    replaceFragment(new HomeFragment());
                    binding.textviewHeader.setText("HOME");
                    break;
                case R.id.menu_chart:
                    replaceFragment(new StatisticFragment());
                    binding.textviewHeader.setText("STATISTC");
                    break;
                case R.id.menu_money:
                    replaceFragment(new ExpenseFragment());
                    binding.textviewHeader.setText("EXPENSE");
                    break;
                case R.id.menu_calendar:
                    replaceFragment(new CalendarFragment());
                    binding.textviewHeader.setText("CALENDAR");
                    break;
                case R.id.menu_profile:
                    replaceFragment(new ProfileFragment());
                    binding.textviewHeader.setText("PROFILE");
                    break;
            }
            return true;
        });
        Intent intent = getIntent();
        //Database database = (Database) intent.getSerializableExtra("db");
        UserInformation userInformation = (UserInformation) intent.getSerializableExtra("user");
        Toast.makeText(getApplicationContext(),String.valueOf(userInformation.getUserID()),Toast.LENGTH_LONG).show();
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}