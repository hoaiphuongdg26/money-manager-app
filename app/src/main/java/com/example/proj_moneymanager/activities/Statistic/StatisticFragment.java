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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.AsyncTasks.readBillFromLocalStorage;
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.databinding.FragmentStatisticBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StatisticFragment extends Fragment {
    FragmentStatisticBinding binding;
    private Button monthYearText, Btn_cal_week, Btn_cal_month, Btn_cal_year;
    private DatePickerDialog datePickerDialog;
    long UserID;
    LocalDate Day, firstWeekDay;
    ArrayList<Bill> arrBill_All;
    ArrayList<Category> categories;
    ArrayList<ContentValues> arrMonths, arrYears, arrMonth, arrWeek, arrCategory;
    BarChart MPbarChart;
    PieChart MPPieChart1, MPPieChart2;

    int[] CategoryColorArray;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStatisticBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        MPbarChart = binding.barChart;
        MPPieChart1 = binding.IncomeExpensePieChart;
        MPPieChart2 = binding.CategoryPieChart;
        UserID = getArguments().getLong("UserID", 0);

        // Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());
        //Gán 3 button tính
        Btn_cal_week = binding.btnCalWeek;
        Btn_cal_month = binding.btnCalMonth;
        Btn_cal_year = binding.btnCalYear;

        arrBill_All = new ArrayList<>();
        categories = new ArrayList<>();
        readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(),categories);
        readCategoryFromLocalStorage.execute();
        CategoryColorArray = new int[]{
                ContextCompat.getColor(getContext(), R.color.MidnightBlue),ContextCompat.getColor(getContext(), R.color.light_gray2),
                ContextCompat.getColor(getContext(), R.color.blue), ContextCompat.getColor(getContext(), R.color.NavyBlue),
                ContextCompat.getColor(getContext(), R.color.black_blue), ContextCompat.getColor(getContext(), R.color.teal_200),
                ContextCompat.getColor(getContext(), R.color.light_blue), ContextCompat.getColor(getContext(), R.color.purple_200),
                ContextCompat.getColor(getContext(), R.color.light_gray), ContextCompat.getColor(getContext(), R.color.purple_700),
                ContextCompat.getColor(getContext(), R.color.white), ContextCompat.getColor(getContext(), R.color.red),
                ContextCompat.getColor(getContext(), R.color.light_silver),
        };
        //Lấy tất cả các bill của user
//        readBillFromLocalStorage readBillFromLocalStorage = new readBillFromLocalStorage(getContext(), arrBill_All);
//        readBillFromLocalStorage.execute(-1,-1,-1);

        calculate(false,true);
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

        return view;
    }
    private void getDataset(int day, int month, int year, String calBy){
            //Ngày trong tháng
            if(Objects.equals(calBy, "Month")){
                LocalDate localDate = LocalDate.of(year+1,month+1,1);
                arrMonth = new ArrayList<>();
                for(int i = 0; i < localDate.lengthOfMonth(); i++){
                    if(i <= localDate.lengthOfMonth()){
                        ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,i + 1,month,year,"Day","All",getContext());
                        arrMonth.add(contentValues);
                    }
                }
                arrCategory = new ArrayList<>();
                for(Category c:categories){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,month,year,calBy,c.getID(),getContext());
                    ContentValues CategoryContentvalue = new ContentValues();
                    CategoryContentvalue.put(c.getName(),(double)contentValues.get("Total"));
                    arrCategory.add(CategoryContentvalue);
                }
            }
            //Ngày trong tuần
            if(Objects.equals(calBy, "Week")){
                arrWeek = new ArrayList<>();
                for(int i = 0; i <7; i++){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,firstWeekDay.plusDays(i).getDayOfMonth(),
                            firstWeekDay.plusDays(i).getMonthValue()-1,firstWeekDay.plusDays(i).getYear()-1,"Day","All",getContext());
                    arrWeek.add(contentValues);
                }
                arrCategory = new ArrayList<>();
                for(Category c:categories){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,firstWeekDay.getDayOfMonth(),
                            firstWeekDay.getMonthValue()-1,firstWeekDay.getYear()-1,calBy,c.getID(),getContext());
                    ContentValues CategoryContentvalue = new ContentValues();
                    CategoryContentvalue.put(c.getName(),(double)contentValues.get("Total"));
                    arrCategory.add(CategoryContentvalue);
                }
            }
            if(calBy.equals("Year")){
                //Tháng trong năm
                arrMonths = new ArrayList<>();
                for(int i = 0; i < 12; i++) {
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,i,year,"Month","All",getContext());
                    arrMonths.add(contentValues);
                }
                arrCategory = new ArrayList<>();
                for(Category c:categories){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,1,year,calBy,c.getID(),getContext());
                    ContentValues CategoryContentvalue = new ContentValues();
                    CategoryContentvalue.put(c.getName(),(double)contentValues.get("Total"));
                    arrCategory.add(CategoryContentvalue);
                }
            }

            //Tất cả các năm
        if (arrBill_All.size() > 0) {

            arrYears = new ArrayList<>();
            int first = arrBill_All.get(0).getDatetime().getYear();
            int last = arrBill_All.get(arrBill_All.size()-1).getDatetime().getYear();
            for(int i = first;i <= last;i++){
                ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,day,month,i,"Year","All",getContext());
                arrYears.add(contentValues);
            }
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
        String calBy;
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
            Day = LocalDate.of(DateTime.getYear() + 1, Month.of(month +1),day);
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
            firstWeekDay = Day.minusDays(firstDayOfWeek);
            ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID, day,
                    month, DateTime.getYear(), calBy, "All",getContext());
            getDataset(day, month,DateTime.getYear(),calBy);
            binding.textviewIncome.setText(MainActivity.formatCurrency((double)contentValues.get("Income")));
            binding.textviewExpense.setText(MainActivity.formatCurrency((double)contentValues.get("Expense")));
            binding.textviewTotal.setText(MainActivity.formatCurrency((double)contentValues.get("Total")));

            if((double)contentValues.get("Income")!=0|| (double)contentValues.get("Expense")!=0){
                DrawBarChartIncomeExpense(calBy);
                DrawPieChart1(calBy,(double)contentValues.get("Income"),(double)contentValues.get("Expense"));
                DrawPieChart2(calBy);
            }else {
                MPbarChart.clear();
                MPPieChart1.clear();
                MPPieChart2.clear();
                MPPieChart1.setNoDataText("No bill data for this "+ calBy);
                MPPieChart2.setNoDataText("No bill data for this "+ calBy);
                MPbarChart.setNoDataText("No bill data for this "+ calBy);
            }
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
        if(Objects.equals(calBy, "Year")){
            for (ContentValues c : arrMonths) {
                barEntries.add(new BarEntry(arrMonths.indexOf(c)+1,-1*Float.parseFloat(String.valueOf(c.get("Expense")))));
            }
        }
        if(Objects.equals(calBy, "Month")){
            for (ContentValues c : arrMonth) {
                barEntries.add(new BarEntry(arrMonth.indexOf(c) + 1,-1*Float.parseFloat(String.valueOf( c.get("Expense")))));
            }
        }
        if(Objects.equals(calBy, "Week")){
            for (ContentValues c : arrWeek) {
                barEntries.add(new BarEntry(arrWeek.indexOf(c),-1*Float.parseFloat(String.valueOf( c.get("Expense")))));
            }
        }
        return barEntries;
    }
    private void DrawBarChartIncomeExpense(String calBy){
        BarDataSet income = new BarDataSet(barEntriesIncome(calBy),"Income");
        BarDataSet expense = new BarDataSet(barEntriesExpense(calBy), "Expense");
        income.setColor(ContextCompat.getColor(getContext(), R.color.teal_700));
        expense.setColor(ContextCompat.getColor(getContext(), R.color.orange));
        BarData barData = new BarData(income,expense);
//        MPbarChart = new BarChart(getContext());
        MPbarChart.clear();
        MPbarChart.setData(barData);
        //Lấy data cho tung độ, hoành độ
        String[] timeLabel = new String[]{};
        float visibleRangeMaximum = 5;
        if(Objects.equals(calBy, "Month")){
            visibleRangeMaximum = 7;
            timeLabel = null;
            timeLabel = new String[31];
            for(int i=0;i<arrMonth.size();i++) timeLabel[i] = String.valueOf(i+1);
        }
        if(Objects.equals(calBy, "Year")) {
            visibleRangeMaximum = 4;
            timeLabel = null;
            timeLabel = new String[12];
            for (int i = 0; i < 12; i++) timeLabel[i] = getMonthFormat(i + 1);
        }
        if(Objects.equals(calBy, "Week")){
            visibleRangeMaximum = 4;
            timeLabel = null;
            timeLabel = new String[] {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        }
        //Định dạng bar cho đẹp
        //Định dạng hoành độ
        XAxis xAxis = MPbarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeLabel));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        MPbarChart.setDragEnabled(true);
        //MPbarChart.setVisibleXRangeMaximum(visibleRangeMaximum);
        MPbarChart.setVisibleXRange(4,visibleRangeMaximum);
        MPbarChart.setAutoScaleMinMaxEnabled(true);
        float barSpace = 0.10f;
        float groupSpace = 0.10f;
        barData.setBarWidth(0.35f);

        //Barchart description
        Description description = new Description();
        description.setText("");
        MPbarChart.setDescription(description);
        //Đặt range của hoành độ
        MPbarChart.getXAxis().setAxisMinimum(0);
        MPbarChart.getXAxis().setAxisMaximum(0 + MPbarChart.getBarData().getGroupWidth(groupSpace,barSpace)*timeLabel.length);
        //Range tung độ
        MPbarChart.getAxisLeft().setAxisMinimum(0);
        MPbarChart.groupBars(0,groupSpace,barSpace);
        MPbarChart.setDoubleTapToZoomEnabled(false);
        MPbarChart.invalidate();
    }
    private ArrayList<PieEntry> pieEntriesExpense(double Income,double Expense){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) Income,"Income"));
        pieEntries.add(new PieEntry((float) - Expense,"Expense"));
        return pieEntries;
    }
    private void DrawPieChart1(String calBy,double Income, double Expense) {
        PieDataSet pieDataSet = new PieDataSet(pieEntriesExpense(Income,Expense),"");
        int[] color = new int[]{ContextCompat.getColor(getContext(), R.color.teal_700),ContextCompat.getColor(getContext(), R.color.orange)};
        pieDataSet.setColors(color);
        pieDataSet.setValueTextSize(12);

        PieData pieData = new PieData(pieDataSet);
        MPPieChart1.setData(pieData);
        MPPieChart1.setDrawEntryLabels(false);
        MPPieChart1.setUsePercentValues(true);
        MPPieChart1.setUsePercentValues(true);
        MPPieChart1.setCenterText("This "+ calBy);
        MPPieChart1.setCenterTextColor(Color.BLUE);
        MPPieChart1.invalidate();
    }
    private ArrayList<PieEntry> pieEntryCategory(ArrayList<ContentValues> contentValues){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for(int i = 0;i<contentValues.size();i++){
            double total =  (double)contentValues.get(i).get(categories.get(i).getName());
            Toast.makeText(getContext(),total+ "",Toast.LENGTH_SHORT).show();
            if(total< 0 ) total *=-1;
            pieEntries.add(new PieEntry((float) total,categories.get(i).getName()));
        }
        return pieEntries;
    }
    private void DrawPieChart2(String calBy){
        PieDataSet pieDataSet = new PieDataSet(pieEntryCategory(arrCategory),"");
        pieDataSet.setColors(CategoryColorArray);
        pieDataSet.setValueTextSize(10);

        PieData pieData = new PieData(pieDataSet);
        MPPieChart2.setData(pieData);
        MPPieChart2.setDrawEntryLabels(false);
        MPPieChart2.setUsePercentValues(true);
        MPPieChart2.setUsePercentValues(true);
        MPPieChart2.setCenterText("This "+ calBy);
        MPPieChart2.setCenterTextColor(Color.BLUE);
        MPPieChart2.invalidate();
    }
}