package com.example.proj_moneymanager.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Setting.MoreFragment;
import com.example.proj_moneymanager.activities.Setting.SettingFragment;
import com.example.proj_moneymanager.databinding.FragmentHomeBinding;
public class HomeFragment extends Fragment {
    private FragmentHomeBinding homeBinding;
    long UserID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            UserID = getArguments().getLong("UserID", 0);
        }
        // Bắt sự kiện click vào button "setting" sau khi view đã được tạo
        ImageButton imagebuttonSetting = homeBinding.buttonSetting;
        imagebuttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi sự kiện khi click
                onSettingButtonClick();
            }
        });
        // Bắt sự kiện click vào button "more" sau khi view đã được tạo
        ImageButton imagebuttonMore = homeBinding.buttonMore;
        imagebuttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi sự kiện khi click
                onMoreButtonClick();
            }
        });
        // Bắt sự kiện click vào button "calendar" sau khi view đã được tạo
        ImageButton imagebuttonCalendar = homeBinding.buttonPlan;
        imagebuttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thông báo cho MainActivity khi nút được nhấp
                if (calendarButtonClickListener != null) {
                    calendarButtonClickListener.onCalendarButtonClick(UserID);
                }
            }
        });
        // Bắt sự kiện click vào button "expense" sau khi view đã được tạo
        ImageButton imagebuttonExpense = homeBinding.buttonIncome;
        imagebuttonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thông báo cho MainActivity khi nút được nhấp
                if (expenseButtonClickListener != null) {
                    expenseButtonClickListener.onExpenseButtonClick(UserID);
                }
            }
        });
        // Bắt sự kiện click vào button "expense" sau khi view đã được tạo
        ImageButton imagebuttonStatistic = homeBinding.buttonStatistic;
        imagebuttonStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thông báo cho MainActivity khi nút được nhấp
                if (statisticButtonClickListener != null) {
                    statisticButtonClickListener.onStatisticButtonClick(UserID);
                }
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
    public void onMoreButtonClick (){
        MoreFragment moreFragment = new MoreFragment();

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, moreFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    public interface OnCalendarButtonClickListener {
        void onCalendarButtonClick(long userID);
    }
    private OnCalendarButtonClickListener calendarButtonClickListener;

    public interface OnExpenseButtonClickListener {
        void onExpenseButtonClick(long userID);
    }
    private OnExpenseButtonClickListener expenseButtonClickListener;

    public interface OnStatisticButtonClickListener {
        void onStatisticButtonClick(long userID);
    }
    private OnStatisticButtonClickListener statisticButtonClickListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Lưu ý: Dùng instanceof để kiểm tra xem context có thực sự triển khai interface hay không
            if (context instanceof OnCalendarButtonClickListener) {
                calendarButtonClickListener = (OnCalendarButtonClickListener) context;
            } else {
                throw new ClassCastException(context.toString() + " must deploy OnCalendarButtonClickListener");
            }

            if (context instanceof OnExpenseButtonClickListener) {
                expenseButtonClickListener = (OnExpenseButtonClickListener) context;
            } else {
                throw new ClassCastException(context.toString() + " must deploy OnExpenseButtonClickListener");
            }
            if (context instanceof OnStatisticButtonClickListener) {
                statisticButtonClickListener = (OnStatisticButtonClickListener) context;
            } else {
                throw new ClassCastException(context.toString() + " must deploy OnStatisticButtonClickListener");
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must deploy OnCalendarButtonClickListener, OnExpenseButtonClickListener, OnStatisticButtonClickListener");
        }
    }
}