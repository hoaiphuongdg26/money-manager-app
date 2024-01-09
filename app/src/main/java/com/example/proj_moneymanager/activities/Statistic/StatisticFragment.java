package com.example.proj_moneymanager.activities.Statistic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.AsyncTasks.readBillFromLocalStorage;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.databinding.FragmentStatisticBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticFragment extends Fragment {
    FragmentStatisticBinding binding;
    private Button monthYearText, Btn_cal_week, Btn_cal_month, Btn_cal_year;
    private DatePickerDialog datePickerDialog;
    long UserID;
    LocalDate Day;
    int maxDayofMonth, firstWeekday, lastWeekDay;
    ListView lv_historyOption;
    ArrayList<Bill> arrBill_All;
    ArrayList<ContentValues> arrMonths, arrYears, arrMonth, arrWeek;
    private List<String> xValues;
    BarChart MPbarChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStatisticBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        MPbarChart = binding.barChart;
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

        //Xử lý History Adapter cho listview
        lv_historyOption = binding.lvHistory;

//        BillAdapter billAdapter = new BillAdapter(
//                requireActivity(),
//                arrBill_Years
//        );
//        lv_historyOption.setAdapter(billAdapter);
        return view;
    }
    private void getDataset(int day, int month, int year, String calBy){
        arrBill_All = new ArrayList<Bill>();
        //Lấy tất cả các bill của user
        readBillFromLocalStorage readBillFromLocalStorage = new readBillFromLocalStorage(getContext(), arrBill_All);
        readBillFromLocalStorage.execute(-1,-1,-1);

        //Ngày trong tháng
        if(calBy == "Month"){
            arrMonth = new ArrayList<>();
            for(int i = 0; i < maxDayofMonth; i++){
                ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,i + 1,month,year,"Day",getContext());
                arrMonth.add(contentValues);
            }
        }
        //Ngày trong tuần
        if(calBy == "Week"){
            arrWeek = new ArrayList<>();
            for(int i = 0; i <7; i++){
                ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,Day.plusDays(i).getDayOfMonth(),month,year,"Day",getContext());
                arrWeek.add(contentValues);
            }
        }
        //Tháng trong năm
        arrMonths = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,day,i,year,"Month",getContext());
            arrMonths.add(contentValues);
        }
        //Tất cả các năm
        arrYears = new ArrayList<>();
        int first = arrBill_All.get(0).getDatetime().getYear();
        int last = arrBill_All.get(arrBill_All.size()-1).getDatetime().getYear();
        for(int i = first;i <= last;i++){
            ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,day,month,i,"Year",getContext());
            arrYears.add(contentValues);
        }
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
            Day = LocalDate.of(DateTime.getYear() + 1, Month.of(month+1),day);
            maxDayofMonth = Day.lengthOfMonth();
            int firstDayOfWeek;
            switch(Day.getDayOfWeek()){
                case MONDAY:
                    firstDayOfWeek = 1;
                    break;
                case TUESDAY:
                    firstDayOfWeek = 2;
                    break;
                case WEDNESDAY:
                    firstDayOfWeek = 3;
                    break;
                case THURSDAY:
                    firstDayOfWeek = 4;
                    break;
                case FRIDAY:
                    firstDayOfWeek = 5;
                    break;
                case SATURDAY:
                    firstDayOfWeek = 6;
                    break;
                default:
                    firstDayOfWeek = 0;
                    break;
            }
            Day = Day.minusDays(firstDayOfWeek);
            ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID, day,
                    month, DateTime.getYear(), calBy, getContext());
            getDataset(day, month,DateTime.getYear(),calBy);
            binding.textviewIncome.setText(String.valueOf(contentValues.get("Income")));
            binding.textviewExpense.setText(String.valueOf(contentValues.get("Expense")));
            binding.textviewTotal.setText(String.valueOf(contentValues.get("Total")));
            DrawChartIncomeExpense(calBy);
        }catch (ParseException e){

        }
    }
    //Xử lí Chart (Bar dọc)
    //3 Dataset theo Income và Expense: week, month, months
    //Hàm calculate sẽ gọi tới nó
    private ArrayList<BarEntry> barEntriesIncome(String calBy){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        if(calBy == "Year"){
            for (ContentValues c : arrMonths) {
                barEntries.add(new BarEntry(arrMonths.indexOf(c)+1,Float.parseFloat(String.valueOf(c.get("Income")))));
            }
        }
        if(calBy == "Month"){
            for (ContentValues c : arrMonth) {
                barEntries.add(new BarEntry(arrMonth.indexOf(c) + 1,Float.parseFloat(String.valueOf( c.get("Income")))));
            }
        }
        if(calBy == "Week"){
            for (ContentValues c : arrWeek) {
                barEntries.add(new BarEntry(arrWeek.indexOf(c)+1,Float.parseFloat(String.valueOf( c.get("Income")))));
            }
        }
        return barEntries;
    }
    private ArrayList<BarEntry> barEntriesExpense(String calBy){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        if(calBy == "Year"){
            for (ContentValues c : arrMonths) {
                barEntries.add(new BarEntry(arrMonths.indexOf(c)+1,-1*Float.parseFloat(String.valueOf(c.get("Expense")))));
            }
        }
        if(calBy == "Month"){
            for (ContentValues c : arrMonth) {
                barEntries.add(new BarEntry(arrMonth.indexOf(c) + 1,-1*Float.parseFloat(String.valueOf( c.get("Expense")))));
            }
        }
        if(calBy == "Week"){
            for (ContentValues c : arrWeek) {
                barEntries.add(new BarEntry(arrWeek.indexOf(c),-1*Float.parseFloat(String.valueOf( c.get("Expense")))));
            }
        }
        return barEntries;
    }
    private void DrawChartIncomeExpense(String calBy){
        BarDataSet income = new BarDataSet(barEntriesIncome(calBy),"Income");
        BarDataSet expense = new BarDataSet(barEntriesExpense(calBy), "Expense");
        income.setColor(R.color.teal_700);
        expense.setColor(R.color.orange);
        BarData barData = new BarData(income,expense);
        MPbarChart.setData(barData);
        //Định dạng bar cho đẹp
        MPbarChart.setDragEnabled(true);
        MPbarChart.groupBars(0,0.44f,0.08f);
        MPbarChart.invalidate();
    }
}