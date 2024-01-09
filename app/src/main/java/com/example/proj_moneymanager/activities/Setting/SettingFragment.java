package com.example.proj_moneymanager.activities.Setting;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.SwipeDetector;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.databinding.DialogLanguageSettingBinding;
import com.example.proj_moneymanager.databinding.FragmentSettingBinding;

import java.util.ArrayList;

public class SettingFragment extends Fragment {
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
        appConfig = new AppConfig(getContext());
        //appConfig.loadLocale();

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Xử lý Setting Adapter cho listview
        lv_settingOption = binding.lvOptSetting;
        arr_settingOption = new ArrayList<Setting_Option>();

        arr_settingOption.add(new Setting_Option(getString(R.string.Language), R.drawable.icon_language));
        arr_settingOption.add(new Setting_Option(getString(R.string.Theme), R.drawable.icon_phone));
        arr_settingOption.add(new Setting_Option(getString(R.string.Text_Formatting), R.drawable.icon_text));

        SettingAdapter SettingAdapter = new SettingAdapter(
                getActivity(),
                arr_settingOption
        );
        lv_settingOption.setAdapter(SettingAdapter);
        //Xử lý vuốt màn hình
        // Khởi tạo SwipeDetector với Setting làm currentActivity và MainActivity.class làm targetActivity
        swipeDetector = new SwipeDetector(getContext(), MainActivity.class);
        binding.lvOptSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    // Lấy ra mục được chọn từ Adapter
                    Setting_Option selectedOption = (Setting_Option) SettingAdapter.getItem(position);
                    // Hiển thị dialog hoặc thực hiện các xử lý tương ứng với mục đã chọn
                    dialogLanguageSetting(selectedOption, position);
                }
                else if(position == 1) {
                    //setting theme
                }
                else {
                    //setting text format
                }
            }
        });

        return view;
    }
    public void dialogLanguageSetting(final Setting_Option item, int position) {
        final Dialog dialog = new Dialog(getContext());
        // Gán layout cho Dialog
        @NonNull DialogLanguageSettingBinding bindingLanguageSetting = DialogLanguageSettingBinding.inflate(getLayoutInflater());
        View viewLanguageSetting = bindingLanguageSetting.getRoot();
        dialog.setContentView(viewLanguageSetting);
        //set checkbox
        if(appConfig.getCurrentLanguage().equals("vi")) {
            bindingLanguageSetting.checkboxVieLanguage.setChecked(true);
        }
        else {
            bindingLanguageSetting.checkboxEngLanguage.setChecked(true);
        }
        // Cấu hình kích thước Dialog
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            // Lấy kích thước màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //int dialogWidth = (int) (displayMetrics.widthPixels * 0.8);
            //int dialogHeight = (int) (displayMetrics.heightPixels * 0.35);
            // Đặt kích thước cho Dialog
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        bindingLanguageSetting.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bindingLanguageSetting.checkboxEngLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bindingLanguageSetting.checkboxVieLanguage.setChecked(false);
                }
            }
        });
        bindingLanguageSetting.checkboxVieLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bindingLanguageSetting.checkboxEngLanguage.setChecked(false);
                }
            }
        });
        bindingLanguageSetting.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bindingLanguageSetting.checkboxVieLanguage.isChecked()) {
                    appConfig.setLocale("vi");
                    recreate(getActivity());
                }
                else {
                    appConfig.setLocale("eng");
                    recreate(getActivity());
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Gửi sự kiện chạm (touch event) tới SwipeDetector để xử lý vuốt
        swipeDetector.gestureDetector.onTouchEvent(event);
        return false;
    }
}