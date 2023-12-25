package com.example.proj_moneymanager.activities.Plan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.FragmentCalendarBinding;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private Button monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    ListView lv_historyOption;
    ArrayList<History_Option> arr_historyOption;
    private DatePickerDialog datePickerDialog;
    ImageButton btnPreviousMonth;
    ImageButton btnNextMonth;
    FragmentCalendarBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
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
//        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        binding = FragmentCalendarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        //Xử lý Calendar
        initWidgets(view);
        selectedDate = LocalDate.now();
        setMonthView();

        btnPreviousMonth = binding.btnPreviousMonth;
        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                previousMonthAction(v);
            }
        });

        btnNextMonth = binding.btnNextMonth;
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                nextMonthAction(v);
            }
        });

        //Xử lý chọn tháng nhanh
        initDatePicker(view);
//        monthYearText = view.findViewById(R.id.btn_datetime_detail);
        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());

        //Xử lý History Adapter cho listview
        lv_historyOption = view.findViewById(R.id.lv_optHistory);
        arr_historyOption = new ArrayList<>();
        //Chỗ này sau này sẽ lấy từ db ra đổ vào array
//        arr_historyOption.add(new History_Option("Food", "Breakfast", R.drawable.btn_food,"-25,000"));
//        arr_historyOption.add(new History_Option("Food", "Snack", R.drawable.btn_food,"-5,000"));
        readFromLocalStorage();
        return view;
    }

    private void updateCalendar(LocalDate newDate) {
        selectedDate = newDate;
        setMonthView();
    }
    private void initWidgets(View view)
    {
        calendarRecyclerView = binding.calendarRecyclerView;
        monthYearText = binding.btnDatetimeDetail;
    }
    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }
    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }
    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        updateCalendar(selectedDate);
    }
    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        updateCalendar(selectedDate);
    }
    //Xử lý sự kiện khi ấn vào 1 ngày bất kỳ trên lịch
    @Override
    public void onItemClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
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
                // Gọi phương thức để cập nhật Calendar
                updateCalendar(LocalDate.of(year, month, day));
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(requireContext(), style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        monthYearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
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
    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
    public void readFromLocalStorage() {
        arr_historyOption.clear(); // Xóa dữ liệu hiện tại để cập nhật từ đầu

        DbHelper dbHelper = new DbHelper(requireContext()); // Sửa lỗi: sử dụng requireContext() thay vì this
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

//        int columnIndexCategoryID = cursor.getColumnIndex(DbContract.TABLE_BILL_CATEGORYID);
        int columnIndexNote = cursor.getColumnIndex(DbContract.TABLE_BILL_NOTE);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.TABLE_BILL_DATETIME);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.TABLE_BILL_MONEY);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexNote != -1 && columnIndexMoney != -1) {
//                int categoryID = cursor.getInt(columnIndexCategoryID);
                double money = cursor.getDouble(columnIndexMoney);
                String note = cursor.getString(columnIndexNote);

                // Tạo đối tượng History_Option từ dữ liệu cơ sở dữ liệu
                // Bạn cần điều chỉnh dòng dưới tùy thuộc vào cấu trúc của lớp History_Option
                History_Option historyOption = new History_Option("Test", note, R.drawable.btn_food, String.valueOf(money));
                //Sau này dùng Bill khi đã xử lý được image của category
//                Bill bill = new Bill()
                // Thêm vào danh sách
                arr_historyOption.add(historyOption);
            } else {
                // Handle the case where the column indices are not found
                // Bạn có thể log lỗi, ném một exception, hoặc xử lý nó một cách nào đó
            }
        }
        cursor.close();
        dbHelper.close();

        // Sau khi đọc xong dữ liệu từ cơ sở dữ liệu, cập nhật Adapter để hiển thị
        HistoryAdapter historyAdapter = new HistoryAdapter(
                requireActivity(),
                arr_historyOption
        );
        lv_historyOption.setAdapter(historyAdapter);
    }

}
