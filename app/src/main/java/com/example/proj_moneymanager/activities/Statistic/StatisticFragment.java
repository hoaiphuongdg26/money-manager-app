package com.example.proj_moneymanager.activities.Statistic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.activities.Plan.BillAdapter;
import com.example.proj_moneymanager.databinding.FragmentStatisticBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticFragment extends Fragment {
    FragmentStatisticBinding binding;
    private Button monthYearText, Btn_cal_week, Btn_cal_month, Btn_cal_year;
    private DatePickerDialog datePickerDialog;
    long UserID;

    ListView lv_historyOption;
    ArrayList<Bill> arrayListBill;
    private List<String> xValues = Arrays.asList("Maths", "Science","English","IT");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStatisticBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        UserID = getArguments().getLong("UserID", 0);

        // Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());
        //Gán 3 button tính
        Btn_cal_week = binding.btnCalWeek;
        Btn_cal_month = binding.btnCalMonth;
        Btn_cal_year = binding.btnCalYear;
        calculate(false,false);
        Btn_cal_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate(false,true);
            }
        });
        Btn_cal_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate(false,false);
            }
        });
        Btn_cal_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate(true,false);
            }
        });
        // Xử lý BarChart: https://www.youtube.com/watch?v=WdsmQ3Zyn84
        UtilsBarChart();

        //Xử lý History Adapter cho listview
        lv_historyOption = binding.lvHistory;
        arrayListBill = new ArrayList<Bill>();
        //Chỗ này sau này sẽ lấy từ db ra đổ vào array
        //arr_historyOption.add(new History_Option("Procery Shoppping", "15 November, 2023", R.drawable.btn_food,"-230.000",1));
        //arr_historyOption.add(new History_Option("Rental Income", "15 November, 2023", R.drawable.btn_food,"+866.00",1));
        BillAdapter billAdapter = new BillAdapter(
                requireActivity(),
                arrayListBill
        );
        lv_historyOption.setAdapter(billAdapter);
        return view;
    }
    private void UtilsBarChart(){
        BarChart barChart = binding.barChart;
        barChart.getAxisRight().setDrawLabels(false);

        customizeBarChart(barChart);
        populateChartData(barChart);
    }
    private void customizeBarChart(BarChart barChart) {
        // Customize BarChart appearance
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getDescription().setEnabled(false);

        // Add any additional customization based on your needs
    }

    private void populateChartData(BarChart barChart) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 45f));
        entries.add(new BarEntry(1, 80f));
        entries.add(new BarEntry(2, 68f));
        entries.add(new BarEntry(3, 38f));

        BarDataSet dataSet = new BarDataSet(entries, "Subjects");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setDrawValues(true); // Enable/disable values on top of bars

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);

        // Notify the chart that the data has changed
        barChart.invalidate();
    }

    private void initDatePicker(View view)
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                monthYearText.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());

        datePickerDialog = new DatePickerDialog(requireContext(), style, dateSetListener, year, month, day);
        monthYearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    private String makeDateString(int day, int month, int year)
    {
        return day + " "+ getMonthFormat(month) + " " + year;
    }
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "January";
        if(month == 2)
            return "February";
        if(month == 3)
            return "March";
        if(month == 4)
            return "April";
        if(month == 5)
            return "May";
        if(month == 6)
            return "June";
        if(month == 7)
            return "July";
        if(month == 8)
            return "August";
        if(month == 9)
            return "September";
        if(month == 10)
            return "October";
        if(month == 11)
            return "November";
        if(month == 12)
            return "December";

        //default should never happen
        return "JAN";
    }
    private void calculate(boolean byYear, boolean byWeek){
        String datetimeString = monthYearText.getText().toString();
        String calBy = "Month";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        try {
            Date DateTime = dateFormat.parse(datetimeString);
            int month = DateTime.getMonth(), day = DateTime.getDate();
            if(!byYear) {
                if(byWeek)
                    calBy = "Week";
                else calBy = "Month";
            }
            else {
                calBy = "Year";
            }
            ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID, day,
                    month, DateTime.getYear(), calBy, getContext());
            binding.textviewIncome.setText(String.valueOf(contentValues.get("Income")));
            binding.textviewExpense.setText(String.valueOf(contentValues.get("Expense")));
            binding.textviewTotal.setText(String.valueOf(contentValues.get("Total")));
        }catch (ParseException e){

        }
    }
}