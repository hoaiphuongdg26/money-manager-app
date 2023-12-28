package com.example.proj_moneymanager.activities.Statistic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.HistoryAdapter;
import com.example.proj_moneymanager.activities.Plan.History_Option;
import com.example.proj_moneymanager.databinding.FragmentStatisticBinding;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {
    FragmentStatisticBinding binding;
    private Button monthYearText;
    private DatePickerDialog datePickerDialog;

    ListView lv_historyOption;
    ArrayList<History_Option> arr_historyOption;
    private List<String> xValues = Arrays.asList("Maths", "Science","English","IT");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStatisticBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());

        // Xử lý BarChart: https://www.youtube.com/watch?v=WdsmQ3Zyn84
        UtilsBarChart();

        //Xử lý History Adapter cho listview
        lv_historyOption = binding.lvHistory;
        arr_historyOption = new ArrayList<History_Option>();
        //Chỗ này sau này sẽ lấy từ db ra đổ vào array
        arr_historyOption.add(new History_Option("Procery Shoppping", "15 November, 2023", R.drawable.btn_food,"-230.000",1));
        arr_historyOption.add(new History_Option("Rental Income", "15 November, 2023", R.drawable.btn_food,"+866.00",1));
        HistoryAdapter historyAdapter = new HistoryAdapter(
                requireActivity(),
                arr_historyOption
        );
        lv_historyOption.setAdapter(historyAdapter);
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