package com.example.proj_moneymanager.activities.Plan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.databinding.DialogBillEditBinding;
import com.example.proj_moneymanager.databinding.FragmentCalendarBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private Button monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    ListView lv_historyOption;
//    ArrayList<History_Option> arr_historyOption, eachday_historyOption;
    ArrayList<Bill> arrayListBill, eachday_arrayListBill;
    BillAdapter billAdapter;
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

        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());
        tv_income = binding.textviewIncome;
        tv_expense = binding.textviewExpense;
        tv_total = binding.textviewTotal;

        //Xử lý History Adapter cho listview
        lv_historyOption = view.findViewById(R.id.lv_optHistory);
        arrayListBill = new ArrayList<Bill>();
        eachday_arrayListBill = new ArrayList<Bill>();

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
                readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask();
                readFromLocalStorageTask.execute();
            }
        });

        btnNextMonth = binding.btnNextMonth;
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                nextMonthAction(v);
                readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask();
                readFromLocalStorageTask.execute();
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
        arrayListBill = new ArrayList<>();
        eachday_arrayListBill = new ArrayList<>();

        //readFromLocalStorage();
        readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask();
        readFromLocalStorageTask.execute();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //loading the again
                //readFromLocalStorage();
                readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask();
                readFromLocalStorageTask.execute();
            }
        };
        lv_historyOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy ra mục được chọn từ Adapter
                Bill selectedBill = billAdapter.getArrHistoryOption().get(position);
                dialogEditBill(selectedBill, position);
            }
        });
        return view;
    }
    class readFromLocalStorageTask extends AsyncTask<Void, Void, ArrayList<Bill>> {
        public readFromLocalStorageTask() {}

        @Override
        protected void onPostExecute(ArrayList<Bill> arrResult) {
            super.onPostExecute(arrResult);
            billAdapter = new BillAdapter(
                    requireActivity(),
                    arrayListBill
            );
            lv_historyOption.setAdapter(billAdapter);
            billAdapter.notifyDataSetChanged();
//            cursor.close();
//            dbHelper.close();
            Toast.makeText(getContext(), "read data completely", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<Bill> doInBackground(Void... voids) {
            arrayListBill.clear(); // Xóa dữ liệu hiện tại để cập nhật từ đầu

            DbHelper dbHelper = new DbHelper(requireContext()); // Sửa lỗi: sử dụng requireContext() thay vì this
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

            // Lấy data ngày
            String datetimeString = binding.textviewEachDay.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            try {
                Date mDateTime = dateFormat.parse(datetimeString);
                ContentValues contentValues = MoneyCalculate(UserID,mDateTime.getDate(),mDateTime.getMonth(),mDateTime.getYear(),"Month",getContext());
                tv_income.setText(String.valueOf(contentValues.get("Income")));
                tv_expense.setText(String.valueOf(contentValues.get("Expense")));
                tv_total.setText(String.valueOf(contentValues.get("Total")));

                int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry._ID);
                int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
                int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
                int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
                int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
                int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
                int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

                while (cursor.moveToNext()) {
                    // Check if the column indices are valid before accessing the values
                    if (columnIndexNote != -1 && columnIndexMoney != -1) {
                        Date DateTime = new Date(cursor.getLong(columnIndexDatetime));

                        if(DateTime.getMonth()==mDateTime.getMonth()){
                            long billID = cursor.getLong(columnIndexBillID);
                            long userID = cursor.getInt(columnIndexUserID);
                            long categoryID = cursor.getInt(columnIndexCategoryID);
                            double money = cursor.getDouble(columnIndexMoney);
                            String note = cursor.getString(columnIndexNote);
                            int sync = cursor.getInt(columnIndexSyncStatus);
                            // Tạo đối tượng Bill từ dữ liệu cơ sở dữ liệu
                            Bill bill = new Bill(billID, userID,categoryID, note,  DateTime, money, sync);
                            // Thêm vào danh sách
                            arrayListBill.add(bill);
                        }
                    } else {
                        // Handle the case where the column indices are not found
                    }
                }
            } catch (ParseException e) {
                Toast.makeText(getContext(),"Error parsing Datetime",Toast.LENGTH_LONG).show();
                e.printStackTrace();
                // Xử lý khi có lỗi chuyển đổi
            }
            return arrayListBill;
        }
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
        binding.textviewEachDay.setText(1 + " " + monthYearFromDate(selectedDate));
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
            //Hiển thị ngày đã chọn ra textview each day
            binding.textviewEachDay.setText(dayText + " " + monthYearFromDate(selectedDate));
            //chuyển ngày chọn thành kieeur Date
            // Lấy data ngày
            String datetimeString = binding.textviewEachDay.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            try {
                Date DateTime = dateFormat.parse(datetimeString);
                eachday_arrayListBill.clear();
                ContentValues contentValues = MoneyCalculate(UserID,DateTime.getDate(),DateTime.getMonth(),DateTime.getYear(),"Day",getContext());
                tv_income.setText(MainActivity.formatCurrency((double)contentValues.get("Income")));
                tv_expense.setText(MainActivity.formatCurrency((double)contentValues.get("Expense")));
                tv_total.setText(MainActivity.formatCurrency((double)contentValues.get("Total")));
                ArrayList<Bill> temp = new ArrayList<>();
                //query arraylist history option userid + datetime
                for (Bill bill : arrayListBill) {
                    Date date = bill.getDatetime();
                    assert DateTime != null;
                    if(DateTime.getDate() == date.getDate() && DateTime.getMonth() == date.getMonth() && DateTime.getYear() == date.getYear())
                        //gans vao arraylist
                        eachday_arrayListBill.add(bill);
                }
                //do vao adapter
                billAdapter = new BillAdapter(requireActivity(),eachday_arrayListBill);
                lv_historyOption.setAdapter(billAdapter);
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
    public static String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + year;
    }
    public static String getMonthFormat(int month)
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
    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    public void dialogEditBill(final Bill billItem, int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @NonNull DialogBillEditBinding bindingDialogEdit = DialogBillEditBinding.inflate(getLayoutInflater());
        View viewDialogEdit = bindingDialogEdit.getRoot();
        dialog.setContentView(viewDialogEdit);

        // Thiết lập kích thước cho Dialog
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Có thể thay đổi kích thước ở đây
        }

        // Set thông tin của bill vào dialog để chỉnh sửa
        bindingDialogEdit.edittextNote.setText(billItem.getNote());
        bindingDialogEdit.edittextExpense.setText(String.valueOf(billItem.getMoney()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billItem.getDatetime());
        String datetimeString = billItem.getDatetime().getDate() + " " + makeDateString(billItem.getDatetime().getDate(),
                billItem.getDatetime().getMonth()+1,calendar.get(Calendar.YEAR));
        bindingDialogEdit.btnDatetimeDetail.setText(datetimeString);
        bindingDialogEdit.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bindingDialogEdit.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(getContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("note", bindingDialogEdit.edittextNote.getText().toString());
                values.put("expense", String.valueOf(bindingDialogEdit.edittextExpense.getText().toString()));
                //values.put("categoryID", String.valueOf());
                String whereClause = DbContract.BillEntry.COLUMN_USER_ID + "=? AND " +
                        DbContract.BillEntry.COLUMN_TIMECREATE + "=?";
                String[] whereArgs = new String[]{
                        String.valueOf(billItem.getUserID()),
                        String.valueOf(billItem.getDatetime().getTime())
                };
                // Thực hiện cập nhật dữ liệu vào local db
                database.update(DbContract.BillEntry.TABLE_NAME, values, whereClause, whereArgs);
                // Sau khi cập nhật dữ liệu, đọc lại dữ liệu từ cơ sở dữ liệu và cập nhật lại ListView
                readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask();
                readFromLocalStorageTask.execute();
                //fetch data mới lên remote db
                Cursor cursor = dbHelper.getBill(billItem.getUserID(), billItem.getDatetime(), database);
                int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
                int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
                int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
                int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
                int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
                if(cursor.moveToFirst()) {
                        int userID = cursor.getInt(columnIndexUserID);
                        int categoryID = cursor.getInt(columnIndexCategoryID);
                        String note = cursor.getString(columnIndexNote);
                        Date timeCreate = new Date();
                        if (columnIndexDatetime != -1) {
                            long datetimeInMillis = cursor.getLong(columnIndexDatetime);
                            timeCreate = new Date(datetimeInMillis);
                        }
                        Date finalTimeCreate = timeCreate;

                        double expense = cursor.getDouble(columnIndexMoney);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCBILL,
                                new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String serverResponse = jsonObject.getString("response");
                                        if (serverResponse.equals("OK")) {
                                            Toast.makeText(getContext(), "UPDATE COMPLETELY", Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            //neu server tra về "fail"
                                            Toast.makeText(getContext(),serverResponse,Toast.LENGTH_LONG).show();
                                            Log.d("Update response error",serverResponse);
                                            //khong lam gì cả
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error appropriately (e.g., log or notify the user)
                                Toast.makeText(getContext(), "Fail to sync data", Toast.LENGTH_LONG);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("note", note);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                params.put("timecreate", dateFormat.format(finalTimeCreate));
                                params.put("expense", String.valueOf(expense));
                                params.put("categoryID", String.valueOf(categoryID));
                                params.put("userID", String.valueOf(userID));
                                params.put("method","UPDATE");
                                return params;
                            }
                        };
                        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                        dialog.dismiss();
                    }
                    }});
        bindingDialogEdit.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(getContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                // Xác định điều kiện WHERE để xóa dữ liệu từ local db
                String whereClause = DbContract.BillEntry.COLUMN_USER_ID + "=? AND " +
                        DbContract.BillEntry.COLUMN_TIMECREATE + "=?";
                String[] whereArgs = new String[]{
                        String.valueOf(billItem.getUserID()),
                        String.valueOf(billItem.getDatetime().getTime())
                };
                // Thực hiện xóa dữ liệu từ local db
                database.delete(DbContract.BillEntry.TABLE_NAME, whereClause, whereArgs);
                readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask();
                readFromLocalStorageTask.execute();
                // Gửi yêu cầu xóa dữ liệu tương ứng trên server
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCBILL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String serverResponse = jsonObject.getString("response");
                                    if (serverResponse.equals("OK")) {
                                        Toast.makeText(getContext(), "Delete successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_LONG).show();
                                        Log.d("Delete response error", serverResponse);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Fail to delete data", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        params.put("userID", String.valueOf(billItem.getUserID()));
                        params.put("timecreate", dateFormat.format(billItem.getDatetime()));
                        params.put("method", "DELETE");
                        return params;
                    }
                };
                // Thêm yêu cầu vào hàng đợi của Volley
                MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //Hàm thống kê income, expense total
    //public static -> các activity khác có thẻ dùng lại. vd biểu đồ
    public static ContentValues MoneyCalculate(long userid, int day, int month, int year, String calBy, Context context){
        ContentValues contentValues = new ContentValues();
        LocalDate FirstDayOfWeek = LocalDate.of(year + 1, Month.of(month+1),day);
        int firstDayOfWeek;
        switch(FirstDayOfWeek.getDayOfWeek()){
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
        FirstDayOfWeek = FirstDayOfWeek.minusDays(firstDayOfWeek);
        //Toast.makeText(context,String.valueOf(FirstDayOfWeek.getDayOfMonth())+" - " +String.valueOf(FirstDayOfWeek.plusDays(6).getDayOfMonth()),Toast.LENGTH_LONG).show();

        double Income = 0, Expense = 0, Total;
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
                    if(calBy!="Year"){
                        if(month == DateTime.getMonth()){
                            if(calBy!="Month"){
                                if(calBy == "Week"){
                                    if(isDateInWeek(DateTime.getDate(),FirstDayOfWeek.getDayOfMonth(),FirstDayOfWeek.plusDays(6).getDayOfMonth())){
                                        if(money < 0) Expense += money;
                                        else Income += money;
                                    }
                                }
                                else{
                                    if(day==DateTime.getDate()){
                                        if(money < 0) Expense += money;
                                        else Income += money;
                                    }
                                }
                            }else {
                                if(money < 0) Expense += money;
                                else Income += money;
                            }
                        }
                    }
                    else {
                        if(money < 0) Expense += money;
                        else Income += money;
                    }
                }
            //}
        }
        Total = Income + Expense;
        contentValues.put("Income", Income);
        contentValues.put("Expense", Expense);
        contentValues.put("Total", Total);
        return contentValues;
    }
    public static boolean isDateInWeek(int day, int weekFirst,int weekLast){
        if(weekFirst<weekLast){
            if(weekFirst<=day && day <= weekLast) return true;
            else return false;
        }
        else{
            if(day<weekFirst && day < weekLast) return true;
            return false;
        }
    }
}
