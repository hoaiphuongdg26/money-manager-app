package com.example.proj_moneymanager.activities;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proj_moneymanager.AsyncTasks.readBillFromLocalStorage;
import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.BillAdapter;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.activities.Setting.MoreFragment;
import com.example.proj_moneymanager.activities.Setting.SettingFragment;
import com.example.proj_moneymanager.databinding.FragmentHomeBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding homeBinding;
    long UserID;
    ListView lvTransaction;
    TextView tvNoTransactionFound,tv_min,tv_incomeRate,tv_total;
    ArrayList<Bill> recentbills;
    IntentFilter intentFilter = new IntentFilter();
    BroadcastReceiver receiver;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        progressBar = homeBinding.progressBar;
        tv_min = homeBinding.textviewRateMin;
        tv_incomeRate = homeBinding.textviewRateMid;
        tv_total = homeBinding.textviewRateMax;
        if (getArguments() != null) {
            UserID = getArguments().getLong("UserID", 0);
        }
        LocalDate date = LocalDate.now();
        intentFilter.addAction("GET_BILL_COMPLETE");
        intentFilter.addAction("GET_SERVER_DATA_COMPLETE");
        getActivity().registerReceiver(receiver,intentFilter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("GET_BILL_COMPLETE")){
                    //Toast.makeText(getContext(),"Progress",Toast.LENGTH_SHORT).show();
                    if(recentbills.size()>0) {
                        tvNoTransactionFound.setVisibility(View.INVISIBLE);
                        double income = 0, total = 0;
                        for(Bill b:recentbills){
                            if(b.getMoney()>0) income+=b.getMoney();
                            if(b.getMoney()<0) total-=b.getMoney();
                            else total+=b.getMoney();
                        }
                        double incomeRate = 100*income/total;
                        int progressPercent = (int) incomeRate;
                        progressBar.setProgress(progressPercent);
                        tv_min.setText("0");
                        tv_incomeRate.setText(MainActivity.formatCurrency(income));
                        tv_total.setText(MainActivity.formatCurrency(total));
                    }
                    else{
                        tvNoTransactionFound.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);
                    }
                }
                else{
                    //Toast.makeText(getContext(),"hihihih",Toast.LENGTH_SHORT).show();
                    recentbills = new ArrayList<>();
                    recentbills.clear();
                    readBillFromLocalStorage readBillFromLocalStorage = new readBillFromLocalStorage(getContext(),recentbills);
                    readBillFromLocalStorage.execute(date.getYear()-1900,date.getMonthValue()-1,date.getDayOfMonth());

                    BillAdapter billAdapter = new BillAdapter(requireActivity(),recentbills);
                    lvTransaction.setAdapter(billAdapter);
                    billAdapter.notifyDataSetChanged();
                }
            }
        };
        lvTransaction = homeBinding.lvTodayTransactions;
        tvNoTransactionFound = homeBinding.tvNoTransactionFound;
        tvNoTransactionFound.setVisibility(View.INVISIBLE);
        recentbills = new ArrayList<>();
        recentbills.clear();
        readBillFromLocalStorage readBillFromLocalStorage = new readBillFromLocalStorage(getContext(),recentbills);
        readBillFromLocalStorage.execute(date.getYear()-1900,date.getMonthValue()-1,date.getDayOfMonth());

        BillAdapter billAdapter = new BillAdapter(requireActivity(),recentbills);
        lvTransaction.setAdapter(billAdapter);
        billAdapter.notifyDataSetChanged();

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
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver,intentFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(receiver!=null)
            getActivity().unregisterReceiver(receiver);
    }

}