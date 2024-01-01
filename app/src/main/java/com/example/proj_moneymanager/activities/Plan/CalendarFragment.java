package com.example.proj_moneymanager.activities.Plan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.FragmentCalendarBinding;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private Button monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    ListView lv_historyOption;
    ArrayList<History_Option> arr_historyOption;
    HistoryAdapter historyAdapter;
    private DatePickerDialog datePickerDialog;
    ImageButton btnPreviousMonth;
    ImageButton btnNextMonth;
    FragmentCalendarBinding binding;
    TextView tv_income,tv_expense,tv_total;
    private BroadcastReceiver broadcastReceiver;
    long UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        binding = FragmentCalendarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        UserID = getArguments().getLong("UserID", 0);
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
        tv_income = binding.textviewIncome;
        tv_expense = binding.textviewExpense;
        tv_total = binding.textviewTotal;

        //Xử lý History Adapter cho listview
        lv_historyOption = view.findViewById(R.id.lv_optHistory);
        arr_historyOption = new ArrayList<>();
        //Chỗ này sau này sẽ lấy từ db ra đổ vào array
//        arr_historyOption.add(new History_Option("Food", "Breakfast", R.drawable.btn_food,"-25,000"));
//        arr_historyOption.add(new History_Option("Food", "Snack", R.drawable.btn_food,"-5,000"));
        readFromLocalStorage();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //loading the again
                readFromLocalStorage();
            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(DbContract.UI_UPDATE_BROADCAST));}
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
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
            //Lấy được ngày đã chọn
            //String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            //Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            //Hiển thị ngày đã chọn ra textview each day
            binding.textviewEachDay.setText(dayText + " " + monthYearFromDate(selectedDate));
            //chuyển ngày chọn thành kieeur Date
            // Lấy data ngày
            String datetimeString = binding.textviewEachDay.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            try {
                Date DateTime = dateFormat.parse(datetimeString);
                ContentValues contentValues = MoneyCalculate(UserID,DateTime.getDate(),DateTime.getMonth(),DateTime.getYear(),getContext());
                tv_income.setText("+"+String.valueOf(contentValues.get("Income")));
                tv_expense.setText("-"+String.valueOf(contentValues.get("Expense")));
                tv_total.setText(String.valueOf(contentValues.get("Total")));
                ArrayList<History_Option> temp = new ArrayList<>();
                //query arraylist history option userid + datetime
                for (History_Option historyOption : arr_historyOption) {
                    Date date = historyOption.getDateTime();
                    assert DateTime != null;
                    if(DateTime.getDate() == date.getDate() && DateTime.getMonth() == date.getMonth() && DateTime.getYear() == date.getYear())
                        //gans vao arraylist
                        temp.add(historyOption);
                }
                //do vao adapter
                historyAdapter = new HistoryAdapter(requireActivity(),temp);
                lv_historyOption.setAdapter(historyAdapter);
            } catch (ParseException e) {
                Toast.makeText(getContext(),"Error parsing Datetime",Toast.LENGTH_LONG).show();
                e.printStackTrace();
                // Xử lý khi có lỗi chuyển đổi
            }
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

        //int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry._ID);
        int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
        //int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
        int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexNote != -1 && columnIndexMoney != -1) {
                Date DateTime = new Date(cursor.getLong(columnIndexDatetime));
                int userID = cursor.getInt(columnIndexUserID);
//                int categoryID = cursor.getInt(columnIndexCategoryID);
                double money = cursor.getDouble(columnIndexMoney);
                String note = cursor.getString(columnIndexNote);
                int sync = cursor.getInt(columnIndexSyncStatus);

                // Tạo đối tượng History_Option từ dữ liệu cơ sở dữ liệu
                History_Option historyOption = new History_Option(DateTime, userID,"Test", note, R.drawable.btn_food, String.valueOf(money), sync);
                // Thêm vào danh sách
                arr_historyOption.add(historyOption);
            } else {
                // Handle the case where the column indices are not found
            }
        }

        //adapter.notifyDataSetChanged();


        // Sau khi đọc xong dữ liệu từ cơ sở dữ liệu, cập nhật Adapter để hiển thị
        historyAdapter = new HistoryAdapter(
                requireActivity(),
                arr_historyOption
        );
        lv_historyOption.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }
    //Hàm thống kê income, expense total
    //Nếu ngày = 0 -> thống kê theo tháng
    //Nếu tháng = 0 -> thống kê theo năm
    //Thống kê theo tuần thì chưa biết
    //public static -> các activity khác có thẻ dùng lại. vd biểu đồ
    public static ContentValues MoneyCalculate(long userid, int day, int month, int year, Context context){
        ContentValues contentValues = new ContentValues();
        long Income = 0, Expense = 0, Total;
        DbHelper dbHelper = new DbHelper(context); // Sửa lỗi: sử dụng requireContext() thay vì this
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

        int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);

        while (cursor.moveToNext()){
            //if (columnIndexMoney != -1) {
                Date DateTime = new Date(cursor.getLong(columnIndexDatetime));
                int userID = cursor.getInt(columnIndexUserID);
                double money = cursor.getDouble(columnIndexMoney);
                if(userID == userid && year == DateTime.getYear()){
                    if(month!=-1){
                        if(month == DateTime.getMonth()){
                            if(day!=-1){
                                if(day==DateTime.getDate()){
                                    if(money > 0) Expense += money;
                                    else Income += money;
                                }
                            }else {
                                if(money > 0) Expense += money;
                                else Income += money;
                            }
                        }
                    }
                    else {
                        if(money > 0) Expense += money;
                        else Income += money;
                    }
                }
            //}
        }
        Total = Income - Expense;
        contentValues.put("Income",Income);
        contentValues.put("Expense",Expense);
        contentValues.put("Total",Total);
        return contentValues;
    }
}
