package com.example.proj_moneymanager.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.SettingAdapter;
import com.example.proj_moneymanager.Setting_Option;
import com.example.proj_moneymanager.app.AppConfig;

import java.util.ArrayList;

public class Setting extends AppCompatActivity {
    private AppConfig appConfig;
    ListView lv_settingOption;
    ArrayList<Setting_Option> arr_settingOption;

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

    }
}
