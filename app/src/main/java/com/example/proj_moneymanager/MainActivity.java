package com.example.proj_moneymanager;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proj_moneymanager.Object.UserInformation;
import com.example.proj_moneymanager.activities.Expense.ExpenseFragment;
import com.example.proj_moneymanager.activities.HomeFragment;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.activities.Profile.ProfileFragment;
import com.example.proj_moneymanager.activities.Statistic.StatisticFragment;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.OnCalendarButtonClickListener,
        HomeFragment.OnExpenseButtonClickListener,
        HomeFragment.OnStatisticButtonClickListener{
    ActivityMainBinding binding;
    NetworkMonitor networkMonitor;
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
        UserID = intent.getLongExtra("UserID", 0);

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
                    break;
                case R.id.menu_chart:
                    replaceFragment(statisticFragment);
                    break;
                case R.id.menu_money:
                    replaceFragment(expenseFragment);
                    break;
                case R.id.menu_calendar:
                    replaceFragment(calendarFragment);
                    break;
                case R.id.menu_profile:
                    replaceFragment(profileFragment);
                    break;
            }
            return true;
        });
        Toast.makeText(getApplicationContext(), UserInformation.getFullName(getBaseContext(), UserID), Toast.LENGTH_LONG).show();
        networkMonitor = new NetworkMonitor();
        registerNetworkBroadcastForNougat();

        scheduleAlarm();
        createNotificationChannel();

    }
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(networkMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        else {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(networkMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    public void onBackButtonClick(View view) {
        // Xử lý khi click ImageButton back
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        view.startAnimation(animation);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0) {
            // Nếu có fragment trong Stack, quay lại fragment trước đó
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    public static String formatCurrency(double amount) {
        boolean isNegative = false;
        // Chia thành phần nguyên và phần thập phân
        if(amount<0) {
            amount*=-1;
            isNegative = true;
        }
        long integerPart = (long) amount;
        int decimalPart = (int) ((amount - integerPart) * 100);

        // Định dạng phần nguyên
        String formattedIntegerPart = formatIntegerPart(integerPart);

        // Định dạng phần thập phân
        String formattedDecimalPart = String.format("%02d", decimalPart);

        String result;
        // Kết hợp phần nguyên và phần thập phân
        if(decimalPart!=0)
             result = formattedIntegerPart + "," + formattedDecimalPart;
        else result = formattedIntegerPart;
        return isNegative? "-"+result:result;
    }

    public static String formatIntegerPart(long integerPart) {
        if(integerPart == 0) return "0";
        String result = "";
        long temp = (long) integerPart;
        while(temp > 0){
            String part = "";
            if(temp/1000 > 0)  part = String.format("%03d",temp % 1000);
            else part = String.valueOf(temp % 1000);
            result = part + result;
            temp/=1000;
            if(temp>0) result = "." + result;
        }
        return result;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(networkMonitor == null) networkMonitor = new NetworkMonitor();
        //registerNetworkBroadcastForNougat();
        MyReceiver.saveLastAccessTime(this, System.currentTimeMillis());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkMonitor);
    }
    private void selectTab(int itemId) {
        binding.navBar.setSelectedItemId(itemId);
    }
    @Override
    public void onCalendarButtonClick(long userID) {
        // Xử lý sự kiện click từ HomeFragment
        CalendarFragment calendarFragment = new CalendarFragment();

        // Truyền UserID qua
        Bundle bundle = new Bundle();
        bundle.putLong("UserID", userID);
        calendarFragment.setArguments(bundle);

        // Thay thế fragment khi mục được chọn trong NavBar
        replaceFragment(calendarFragment);
        selectTab(R.id.menu_calendar);

    }

    @Override
    public void onExpenseButtonClick(long userID) {
        // Xử lý sự kiện click từ HomeFragment
        ExpenseFragment expenseFragment = new ExpenseFragment();

        // Truyền UserID qua
        Bundle bundle = new Bundle();
        bundle.putLong("UserID", userID);
        expenseFragment.setArguments(bundle);

        // Thay thế fragment khi mục được chọn trong NavBar
        replaceFragment(expenseFragment);
        selectTab(R.id.menu_money);
    }

    @Override
    public void onStatisticButtonClick(long userID) {
        // Xử lý sự kiện click từ HomeFragment
        StatisticFragment statisticFragment = new StatisticFragment();

        // Truyền UserID qua
        Bundle bundle = new Bundle();
        bundle.putLong("UserID", userID);
        statisticFragment.setArguments(bundle);

        // Thay thế fragment khi mục được chọn trong NavBar
        replaceFragment(statisticFragment);
        selectTab(R.id.menu_chart);
    }
    private void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Intent để gửi tới BroadcastReceiver
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Lên lịch kiểm tra sau 24 giờ
        long interval = 60 * 1000; // 24 giờ
//        long interval = 24 * 60 * 60 * 1000; // 24 giờ
        long startTime = System.currentTimeMillis() + interval;

        // Lên lịch kiểm tra với thời gian bắt đầu và khoảng thời gian lặp
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}