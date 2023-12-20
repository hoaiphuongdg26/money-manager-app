package com.example.proj_moneymanager.activities.Statistic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.HistoryAdapter;
import com.example.proj_moneymanager.activities.Plan.History_Option;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Statistic extends AppCompatActivity{
    private Button monthYearText;
    private DatePickerDialog datePickerDialog;

    ListView lv_historyOption;
    ArrayList<History_Option> arr_historyOption;
    private List<String> xValues = Arrays.asList("Maths", "Science","English","IT");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);

        // Find the TextView in the included layout
        TextView headerTextView = findViewById(R.id.textview_Header);
        headerTextView.setText("STATISTIC");

        //Xử lý chọn tháng nhanh
//        initDatePicker();
//        monthYearText = findViewById(R.id.btn_datetime_detail);
//        monthYearText.setText(getTodaysDate());

        // Xử lý BarChart: https://www.youtube.com/watch?v=WdsmQ3Zyn84
        UtilsBarChart();

        //Xử lý History Adapter cho listview
        lv_historyOption = (ListView) findViewById(R.id.lv_History);
        arr_historyOption = new ArrayList<History_Option>();
        //Chỗ này sau này sẽ lấy từ db ra đổ vào array
        arr_historyOption.add(new History_Option("Procery Shoppping", "15 November, 2023", R.drawable.btn_food,"-230.000"));
        arr_historyOption.add(new History_Option("Rental Income", "15 November, 2023", R.drawable.btn_food,"+866.00"));
        HistoryAdapter historyAdapter = new HistoryAdapter(
                Statistic.this,
                arr_historyOption
        );
        lv_historyOption.setAdapter(historyAdapter);
    }
    private void UtilsBarChart(){
        BarChart barChart = findViewById(R.id.barChart);
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

    private void initDatePicker()
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

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
        return getMonthFormat(month) + " " + year;
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
            return "Octorber";
        if(month == 11)
            return "November";
        if(month == 12)
            return "December";

        //default should never happen
        return "JAN";
    }
}
