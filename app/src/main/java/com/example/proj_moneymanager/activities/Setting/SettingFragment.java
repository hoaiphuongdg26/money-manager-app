package com.example.proj_moneymanager.activities.Setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.SwipeDetector;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.databinding.FragmentSettingBinding;

import java.util.ArrayList;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    private AppConfig appConfig;
    ListView lv_settingOption;
    ArrayList<Setting_Option> arr_settingOption;
    SwipeDetector swipeDetector;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentSettingBinding binding = FragmentSettingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Xử lý Setting Adapter cho listview
        lv_settingOption = binding.lvOptSetting;
        arr_settingOption = new ArrayList<Setting_Option>();

        arr_settingOption.add(new Setting_Option("Language", R.drawable.icon_language));
        arr_settingOption.add(new Setting_Option("Theme", R.drawable.icon_phone));
        arr_settingOption.add(new Setting_Option("Text Formatting", R.drawable.icon_text));

        SettingAdapter SettingAdapter = new SettingAdapter(
                getActivity(),
                arr_settingOption
        );
        lv_settingOption.setAdapter(SettingAdapter);
        //Xử lý vuốt màn hình
        // Khởi tạo SwipeDetector với Setting làm currentActivity và MainActivity.class làm targetActivity
        swipeDetector = new SwipeDetector(getContext(), MainActivity.class);
        lv_settingOption.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        return view;
    }
    public boolean onTouchEvent(MotionEvent event) {
        // Gửi sự kiện chạm (touch event) tới SwipeDetector để xử lý vuốt
        swipeDetector.gestureDetector.onTouchEvent(event);
        return false;
    }
}