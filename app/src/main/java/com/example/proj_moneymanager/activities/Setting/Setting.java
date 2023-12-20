package com.example.proj_moneymanager.activities.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.MainActivity;
import com.example.proj_moneymanager.activities.SwipeDetector;
import com.example.proj_moneymanager.app.AppConfig;

import java.util.ArrayList;

public class Setting extends AppCompatActivity {
    private AppConfig appConfig;
    ListView lv_settingOption;
    ArrayList<Setting_Option> arr_settingOption;
    SwipeDetector swipeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(this);
        setContentView(R.layout.setting);

        //Xử lý Setting Adapter cho listview
        lv_settingOption = (ListView) findViewById(R.id.lv_optSetting);
        arr_settingOption = new ArrayList<Setting_Option>();

        arr_settingOption.add(new Setting_Option("Language", R.drawable.icon_language));
        arr_settingOption.add(new Setting_Option("Theme", R.drawable.icon_phone));
        arr_settingOption.add(new Setting_Option("Text Formatting", R.drawable.icon_text));

        SettingAdapter SettingAdapter = new SettingAdapter(
                Setting.this,
                arr_settingOption
        );
        lv_settingOption.setAdapter(SettingAdapter);
        //Xử lý vuốt màn hình
        // Khởi tạo SwipeDetector với Setting làm currentActivity và MainActivity.class làm targetActivity
        swipeDetector = new SwipeDetector(this, MainActivity.class);

        lv_settingOption.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Gửi sự kiện chạm (touch event) tới SwipeDetector để xử lý vuốt
        swipeDetector.gestureDetector.onTouchEvent(event);
        return false;
    }
}
