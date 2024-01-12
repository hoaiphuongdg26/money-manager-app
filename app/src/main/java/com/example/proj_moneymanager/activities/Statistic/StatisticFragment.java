package com.example.proj_moneymanager.activities.Statistic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.AsyncTasks.readBillFromLocalStorage;
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.databinding.DialogBillEditBinding;
import com.example.proj_moneymanager.databinding.DialogCategoryLinechartBinding;
import com.example.proj_moneymanager.databinding.FragmentStatisticBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

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
    ArrayList<ArrayList<ContentValues>> arrCategoryWeek, arrCategoryYear, arrCategoryMonth;
    BarChart MPbarChart;
    PieChart MPPieChart1, MPPieChart2;
    ContentValues contentValues = new ContentValues();
    int[] CategoryColorArray;
    BroadcastReceiver receiver;
    IntentFilter intentFilter = new IntentFilter();
    calculate cal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStatisticBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        intentFilter.addAction("CALCULATE_COMPLETE");
        getActivity().registerReceiver(receiver,intentFilter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String calBy = intent.getExtras().getString("CalBy");
                if(calBy==null||calBy.isEmpty()){
                    return;
                }
                if((double)contentValues.get("Income")!=0 || (double)contentValues.get("Expense")!=0){
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
            }
        };
        MPbarChart = binding.barChart;
        MPPieChart1 = binding.IncomeExpensePieChart;
        MPPieChart2 = binding.CategoryPieChart;
        MPPieChart1.setNoDataText("Loading data");
        MPPieChart2.setNoDataText("Loading data");
        MPbarChart.setNoDataText("Loading data");
        MPbarChart.getAxisRight().setEnabled(false);
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
                ContextCompat.getColor(getContext(), R.color.red),ContextCompat.getColor(getContext(), R.color.blue),
                 ContextCompat.getColor(getContext(), R.color.NavyBlue),ContextCompat.getColor(getContext(), R.color.light_silver),
                ContextCompat.getColor(getContext(), R.color.black_blue), ContextCompat.getColor(getContext(), R.color.teal_200),
                ContextCompat.getColor(getContext(), R.color.light_blue), ContextCompat.getColor(getContext(), R.color.purple_200),
                ContextCompat.getColor(getContext(), R.color.light_gray), ContextCompat.getColor(getContext(), R.color.purple_700),
                ContextCompat.getColor(getContext(), R.color.white),

        };
        cal = new calculate(requireContext());
        cal.execute(false,false);
        Btn_cal_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = new calculate(requireContext());
                cal.execute(false,true);
            }
        });
        Btn_cal_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = new calculate(requireContext());
                cal.execute(false,false);
            }
        });
        Btn_cal_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal = new calculate(requireContext());
                cal.execute(true,false);
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
                        ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,i + 1,month,year,"Day","All",requireContext());
                        arrMonth.add(contentValues);
                    }
                }
                arrCategory = new ArrayList<>();
                for(Category c:categories){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,month,year,calBy,c.getID(),requireContext());
                    ContentValues CategoryContentvalue = new ContentValues();
                    CategoryContentvalue.put(c.getName(),(double)contentValues.get("Total"));
                    arrCategory.add(CategoryContentvalue);
                }
                arrCategoryMonth = new ArrayList<>();
                for(int i = 0; i < localDate.lengthOfMonth(); i++){
                    ArrayList<ContentValues> dayIndex = new ArrayList<>();
                    for(Category c:categories){
                        ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,i + 1,month,year,"Day",c.getID(),requireContext());
                        dayIndex.add(contentValues);
                    }
                    arrCategoryMonth.add(dayIndex);
                }
            }
            //Ngày trong tuần
            if(Objects.equals(calBy, "Week")){
                arrWeek = new ArrayList<>();
                for(int i = 0; i <7; i++){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,firstWeekDay.plusDays(i).getDayOfMonth(),
                            firstWeekDay.plusDays(i).getMonthValue()-1,firstWeekDay.plusDays(i).getYear()-1,"Day","All",requireContext());
                    arrWeek.add(contentValues);
                }
                arrCategory = new ArrayList<>();
                for(Category c:categories){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,firstWeekDay.getDayOfMonth(),
                            firstWeekDay.getMonthValue()-1,firstWeekDay.getYear()-1,calBy,c.getID(),requireContext());
                    ContentValues CategoryContentvalue = new ContentValues();
                    CategoryContentvalue.put(c.getName(),(double)contentValues.get("Total"));
                    arrCategory.add(CategoryContentvalue);
                }
                arrCategoryWeek = new ArrayList<>();
                for(int i = 0; i<7;i++){
                    ArrayList<ContentValues> dayIndex = new ArrayList<>();
                    for(Category c:categories){
                        ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,firstWeekDay.plusDays(i).getDayOfMonth(),
                                firstWeekDay.plusDays(i).getMonthValue()-1,firstWeekDay.plusDays(i).getYear()-1,"Day",c.getID(),requireContext());
                        dayIndex.add(contentValues);
                    }
                    arrCategoryWeek.add(dayIndex);
                }
            }
            if(calBy.equals("Year")){
                //Tháng trong năm
                arrMonths = new ArrayList<>();
                for(int i = 0; i < 12; i++) {
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,i,year,"Month","All",requireContext());
                    arrMonths.add(contentValues);
                }
                arrCategory = new ArrayList<>();
                for(Category c:categories){
                    ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,1,year,calBy,c.getID(),requireContext());
                    ContentValues CategoryContentvalue = new ContentValues();
                    CategoryContentvalue.put(c.getName(),(double)contentValues.get("Total"));
                    arrCategory.add(CategoryContentvalue);
                }
                arrCategoryYear = new ArrayList<>();
                for(int i = 0; i < 12; i++) {
                    ArrayList<ContentValues> monthIndex = new ArrayList<>();
                    for(Category c:categories){
                        ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,1,i,year,"Month",c.getID(),requireContext());
                        monthIndex.add(contentValues);
                    }
                    arrCategoryYear.add(monthIndex);
                }
            }
            //Tất cả các năm
        if (arrBill_All.size() > 0) {

            arrYears = new ArrayList<>();
            int first = arrBill_All.get(0).getDatetime().getYear();
            int last = arrBill_All.get(arrBill_All.size()-1).getDatetime().getYear();
            for(int i = first;i <= last;i++){
                ContentValues contentValues = CalendarFragment.MoneyCalculate(UserID,day,month,i,"Year","All",requireContext());
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
    private class calculate extends AsyncTask<Boolean,Void,String>{
        Context context;
        String calBy;
        public calculate(Context context){
            this.context = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isAdded() && !isDetached()) {
                Intent intent = new Intent();
                intent.setAction("CALCULATE_COMPLETE");
                intent.putExtra("CalBy", calBy);
                context.sendBroadcast(intent);
            }
        }
        @Override
        protected String doInBackground(Boolean... booleans) {
            if (isAdded() && !isDetached()) {
                boolean byYear = booleans[0], byWeek = booleans[1];
                String datetimeString = monthYearText.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                try {
                    Date DateTime = dateFormat.parse(datetimeString);
                    int month = DateTime.getMonth(), day = DateTime.getDate();
                    if (!byYear) {
                        if (byWeek)
                            calBy = "Week";
                        else calBy = "Month";
                    } else {
                        calBy = "Year";
                    }
                    Day = LocalDate.of(DateTime.getYear() + 1, Month.of(month + 1), day);
                    int firstDayOfWeek;
                    switch (Day.getDayOfWeek()) {
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
                    contentValues = CalendarFragment.MoneyCalculate(UserID, day,
                            month, DateTime.getYear(), calBy, "All", requireContext());
                    getDataset(day, month, DateTime.getYear(), calBy);
                    binding.textviewIncome.setText(MainActivity.formatCurrency((double) contentValues.get("Income")));
                    binding.textviewExpense.setText(MainActivity.formatCurrency((double) contentValues.get("Expense")));
                    binding.textviewTotal.setText(MainActivity.formatCurrency((double) contentValues.get("Total")));
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.Error) + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }
    }
    private ArrayList<BarEntry> barEntriesIncome(String calBy){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        if(calBy.equals("Year")){
            for (ContentValues c : arrMonths) {
                barEntries.add(new BarEntry(arrMonths.indexOf(c)+1,Float.parseFloat(String.valueOf(c.get("Income")))));
            }
        }
        if(calBy.equals("Month")){
            for (ContentValues c : arrMonth) {
                barEntries.add(new BarEntry(arrMonth.indexOf(c) + 1,Float.parseFloat(String.valueOf( c.get("Income")))));
            }
        }
        if(calBy.equals("Week")){
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
            visibleRangeMaximum = 15;
            timeLabel = null;
            timeLabel = new String[31];
            for(int i=0;i<arrMonth.size();i++) timeLabel[i] = String.valueOf(i+1);
        }
        if(Objects.equals(calBy, "Year")) {
            visibleRangeMaximum = 6;
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
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        if(!calBy.equals("Month")) xAxis.setLabelRotationAngle(-35);
        else xAxis.setLabelRotationAngle(0);

        MPbarChart.setDragEnabled(true);
        //MPbarChart.setVisibleXRangeMaximum(visibleRangeMaximum);
        MPbarChart.setVisibleXRange(4,visibleRangeMaximum);
        MPbarChart.setAutoScaleMinMaxEnabled(false);
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
        MPbarChart.animateXY(450,450);
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
        Description description = new Description();
        description.setText("");
        MPPieChart1.setDescription(description);
        MPPieChart1.animateXY(600,700);
        MPPieChart1.invalidate();
        MPPieChart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                MPPieChart1.setCenterText(pe.getLabel() + ":\n"+ e.getY());
                dialogCategoryLineChart(pe.getLabel(),calBy);
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }
    private ArrayList<PieEntry> pieEntryCategory(ArrayList<ContentValues> contentValues){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for(int i = 0;i<contentValues.size();i++){
            double total =  (double)contentValues.get(i).get(categories.get(i).getName());
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
        Description description = new Description();
        description.setText("");
        MPPieChart2.setDescription(description);
        MPPieChart2.animateXY(525,525);
        MPPieChart2.invalidate();
        MPPieChart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                MPPieChart2.setCenterText(pe.getLabel() + ":\n"+ e.getY());
                MPPieChart2.setCenterTextColor(CategoryColorArray[pieDataSet.getEntryIndex(e)]);
            }
            @Override
            public void onNothingSelected() {
                MPPieChart2.setCenterText("This "+ calBy);
                MPPieChart2.setCenterTextColor(Color.BLUE);
            }
        });
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
    public ArrayList<Entry> getLineValues(int categoryIndex, String calBy, String type){
        ArrayList<Entry> data = new ArrayList<>();
        if(calBy.equals("Week")){
            for(int i = 0;i < 7;i++){
                double d = (double) arrCategoryWeek.get(i).get(categoryIndex).get(type);
                if(d<0) d*=-1;
                data.add(new Entry(i, (float) d));
            }
        }
        if(calBy.equals("Month")){
            for(int i = 0;i < arrMonth.size() ;i++){
                double d = (double) arrCategoryMonth.get(i).get(categoryIndex).get(type);
                if(d<0) d*=-1;
                data.add(new Entry(i, (float) d));
            }
        }
        if(calBy.equals("Year")){
            for(int i = 0;i < arrMonths.size();i++){
                double d = (double) arrCategoryYear.get(i).get(categoryIndex).get(type);
                if(d<0) d*=-1;
                data.add(new Entry(i, (float) d));
            }
        }
        return data;
    }
    public void DrawLineChart(LineChart lineChart, String calBy, String type){
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for(Category c: categories){
            LineDataSet lineDataSet = new LineDataSet(getLineValues(categories.indexOf(c),calBy,type),c.getName());
            lineDataSet.setColor(CategoryColorArray[categories.indexOf(c)]);
            lineDataSet.setLineWidth(1.1f);
            lineDataSet.setDrawCircles(false);
            dataSets.add(lineDataSet);
        }
        String[] timeLabel = new String[]{};
        float visibleRangeMaximum = 7;
        if(Objects.equals(calBy, "Month")){
            visibleRangeMaximum = 15;
            timeLabel = null;
            timeLabel = new String[31];
            for(int i=0;i<arrMonth.size();i++) timeLabel[i] = String.valueOf(i+1);
        }
        if(Objects.equals(calBy, "Year")) {
            visibleRangeMaximum = 6;
            timeLabel = null;
            timeLabel = new String[12];
            for (int i = 0; i < 12; i++) timeLabel[i] = getMonthFormat(i + 1);
        }
        if(Objects.equals(calBy, "Week")){
            visibleRangeMaximum = 4;
            timeLabel = null;
            timeLabel = new String[] {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeLabel));
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        lineChart.setVisibleXRangeMaximum(visibleRangeMaximum);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateXY(500,500);
        Description description = new Description();
        description.setText(type);
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.setDescription(description);
        if(!calBy.equals("Month")) xAxis.setLabelRotationAngle(-35);
        else xAxis.setLabelRotationAngle(0);
        lineChart.invalidate();
    }
    public void dialogCategoryLineChart(String type, String calBy) {
        final Dialog dialog = new Dialog(getContext());
        //dialog.setTitle(getString(R.string.Edit_bill));

        @NonNull DialogCategoryLinechartBinding linechartBinding = DialogCategoryLinechartBinding.inflate(getLayoutInflater());
        View viewDialogLineChart = linechartBinding.getRoot();
        dialog.setContentView(viewDialogLineChart);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            // Lấy kích thước màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int dialogWidth = (int) (displayMetrics.widthPixels * 0.95);
            int dialogHeight = (int) (displayMetrics.heightPixels * 0.6);
            // Đặt kích thước cho Dialog
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = dialogWidth;
            layoutParams.height = dialogHeight;
            window.setAttributes(layoutParams);
        }
        linechartBinding.tvLineChartTitle.setText(type+ " of "+calBy);
        DrawLineChart(linechartBinding.MPLineChart,calBy,type);
        dialog.show();
        MPPieChart1.setSelected(false);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (cal != null && !cal.isCancelled()) {
            cal.cancel(true);
        }
    }
}