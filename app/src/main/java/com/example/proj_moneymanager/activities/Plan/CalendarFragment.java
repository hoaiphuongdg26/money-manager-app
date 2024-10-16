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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
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
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Expense.CategoryAdapter;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
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

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, BillAdapter.OnBillItemClickListener, CategoryAdapter.OnCategoryClickListener {

    private Button monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    ListView lv_historyOption;
    String CategoryID;
    ArrayList<Bill> arrayListBill, eachday_arrayListBill;
    ArrayList<Category> arrayListCategory = new ArrayList<Category>();
    BillAdapter billAdapter;
    private DatePickerDialog datePickerDialog;
    ImageButton btnPreviousMonth;
    ImageButton btnNextMonth;
    FragmentCalendarBinding binding;
    private CategoryAdapter categoryAdapter;
    TextView tv_income,tv_expense,tv_total;
    private BroadcastReceiver broadcastReceiver;
    long UserID;
    String billID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getLong("UserID", 0);
        }

        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());
        tv_income = binding.textviewIncome;
        tv_expense = binding.textviewExpense;
        tv_total = binding.textviewTotal;

        //Xử lý History Adapter cho listview
        lv_historyOption = binding.lvOptHistory;
        arrayListBill = new ArrayList<Bill>();
        eachday_arrayListBill = new ArrayList<Bill>();

        //Xử lý Calendar
        initWidgets(view);
        selectedDate = LocalDate.now();
        setMonthView();

        //readFromLocalStorage();
        callReadFromStorageTaskByMonth();

        btnPreviousMonth = binding.btnPreviousMonth;
        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                previousMonthAction(v);

//                readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(), arryListCategory);
//                readCategoryFromLocalStorage.execute();
                callReadFromStorageTaskByMonth();
            }
        });

        btnNextMonth = binding.btnNextMonth;
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                nextMonthAction(v);

//                readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(), arryListCategory);
//                readCategoryFromLocalStorage.execute();
                callReadFromStorageTaskByMonth();
            }
        });
        lv_historyOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy ra mục được chọn từ Adapter
                Bill selectedOption = (Bill) billAdapter.getItem(position);
            }
        });
        //Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());
        tv_income = binding.textviewIncome;
        tv_expense = binding.textviewExpense;
        tv_total = binding.textviewTotal;
        //readFromLocalStorage();
        //callReadFromStorageTaskByMonth();
//        readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(),arryListCategory);
//        readCategoryFromLocalStorage.execute();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //loading the again
//                readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(), arryListCategory);
//                readCategoryFromLocalStorage.execute();
                callReadFromStorageTaskByMonth();

            }
        };
        return view;
    }

    @Override
    public void onBillItemClick(Bill bill, int position) {
        dialogEditBill(bill, position);
    }

    @Override
    public void onCategoryClick(String selectedCategoryId) {
        CategoryID = selectedCategoryId;
    }

    class readFromLocalStorageTask extends AsyncTask<Integer, Void, ArrayList<Bill>> {
        public readFromLocalStorageTask(CalendarFragment calendarFragment) {}

        @Override
        protected void onPostExecute(ArrayList<Bill> arrResult) {
            if (isAdded()) {
                super.onPostExecute(arrResult);
                billAdapter = new BillAdapter(
                        requireActivity(),
                        arrayListBill
                );
                billAdapter.setOnBillItemClickListener(new BillAdapter.OnBillItemClickListener() {
                    @Override
                    public void onBillItemClick(Bill bill, int position) {
                        dialogEditBill(bill, position);
                    }
                });
                lv_historyOption.setAdapter(billAdapter);
                billAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<Bill> doInBackground(Integer... params) {
            arrayListBill.clear(); // Xóa dữ liệu hiện tại để cập nhật từ đầu

            if(isAdded()&&getContext()!=null){
                DbHelper dbHelper = new DbHelper(getContext()); // Sửa lỗi: sử dụng requireContext() thay vì this
                SQLiteDatabase database = dbHelper.getReadableDatabase();
                Cursor cursor = dbHelper.readBillFromLocalDatabase(database);
                // Lấy data ngày
                    int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_ID);
                    int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
                    int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
                    int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
                    int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
                    int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
                    int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

                    try {
                        while (cursor.moveToNext()) {
                            // Check if the column indices are valid before accessing the values
                            if (columnIndexNote != -1 && columnIndexMoney != -1) {
                                Date DateTime = new Date(cursor.getLong(columnIndexDatetime));
                                billID = cursor.getString(columnIndexBillID);
                                UserID = cursor.getInt(columnIndexUserID);
                                String categoryID = cursor.getString(columnIndexCategoryID);
                                long money = cursor.getLong(columnIndexMoney);
                                String note = cursor.getString(columnIndexNote);
                                int sync = cursor.getInt(columnIndexSyncStatus);
                                if (params[0] != -1) {
                                    if (DateTime.getYear() == params[0]) {
                                        if (params[1] != -1) {
                                            if (DateTime.getMonth() == params[1]) {
                                                if (params[2] != -1) {
                                                    if (DateTime.getDate() == params[2]) {
                                                        //Tính theo ngày
                                                        // Tạo đối tượng Bill từ dữ liệu cơ sở dữ liệu
                                                        Bill bill = new Bill(billID, UserID, categoryID, note, DateTime, money, sync);
                                                        // Thêm vào danh sách
                                                        arrayListBill.add(bill);
                                                    }
                                                } else {
                                                    // Tạo đối tượng Bill từ dữ liệu cơ sở dữ liệu
                                                    Bill bill = new Bill(billID, UserID, categoryID, note, DateTime, money, sync);
                                                    arrayListBill.add(bill);
                                                }
                                            }
                                        } else {
                                            //Tính theo năm
                                            // Tạo đối tượng Bill từ dữ liệu cơ sở dữ liệu
                                            Bill bill = new Bill(billID, UserID, categoryID, note, DateTime, money, sync);
                                            arrayListBill.add(bill);
                                        }
                                    }
                                } else {
                                    //lấy tất cả
                                    // Tạo đối tượng Bill từ dữ liệu cơ sở dữ liệu
                                    Bill bill = new Bill(billID, UserID, categoryID, note, DateTime, money, sync);
                                    // Thêm vào danh sách
                                    arrayListBill.add(bill);
                                }
                            } else {
                                // Handle the case where the column indices are not found
                            }
                        }
                    } finally {
                        // Đảm bảo đóng Cursor và Database trong khối finally
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        if (database != null && database.isOpen()) {
                            database.close();
                        }
                    }
            }
            return arrayListBill;
        }
    }
    private void callReadFromStorageTaskByMonth(){
        String datetimeString = binding.textviewEachDay.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
        try {
            Date mDateTime = dateFormat.parse(datetimeString);
            ContentValues contentValues = MoneyCalculate(UserID,mDateTime.getDate(),mDateTime.getMonth(),mDateTime.getYear(), getContext().getString(R.string.Month),"All",getContext());
            tv_income.setText(MainActivity.formatCurrency((double)contentValues.get("Income")));
            tv_expense.setText(MainActivity.formatCurrency((double)contentValues.get("Expense")));
            tv_total.setText(MainActivity.formatCurrency((double)contentValues.get("Total")));
            readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask(CalendarFragment.this);
            readFromLocalStorageTask.execute(mDateTime.getYear(),mDateTime.getMonth(),-1);
        } catch (ParseException e) {
            //Toast.makeText(getContext(),"Error parsing Datetime",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void callReadFromStorageTaskByDay(){
        String datetimeString = binding.textviewEachDay.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        try {
            Date mDateTime = dateFormat.parse(datetimeString);
            ContentValues contentValues = MoneyCalculate(UserID,mDateTime.getDate(),mDateTime.getMonth(),mDateTime.getYear(),"Day", "All",getContext());
            tv_income.setText(MainActivity.formatCurrency((double)contentValues.get("Income")));
            tv_expense.setText(MainActivity.formatCurrency((double)contentValues.get("Expense")));
            tv_total.setText(MainActivity.formatCurrency((double)contentValues.get("Total")));
            readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask(CalendarFragment.this);
            readFromLocalStorageTask.execute(mDateTime.getYear(),mDateTime.getMonth(),mDateTime.getDate());
        } catch (ParseException e) {
            //Toast.makeText(getContext(),"Error parsing Datetime",Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
        binding.textviewEachDay.setText(monthYearFromDate(selectedDate));
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.US);
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
            callReadFromStorageTaskByDay();
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
    private void initDatePicker(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                monthYearText.setText(date);
                // Gọi phương thức để cập nhật Calendar
                updateCalendar(LocalDate.of(year, month, day));

                // Call the method to read category data
                callReadFromStorageTaskByMonth();
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(requireContext(), style, dateSetListener, year, month, day);

        // Customize DatePicker layout for month and year only
        datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);

        // Set max and min date if needed
        // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

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
        dialog.setTitle(getString(R.string.Edit_bill));

        @NonNull DialogBillEditBinding bindingDialogEdit = DialogBillEditBinding.inflate(getLayoutInflater());
        View viewDialogEdit = bindingDialogEdit.getRoot();
        if(billItem.getMoney() > 0) {
            bindingDialogEdit.textviewExpenseIncome.setText(getString(R.string.Income));
        }
        else {
            bindingDialogEdit.textviewExpenseIncome.setText(getString(R.string.Expense));
        }
        // Set thông tin của bill vào dialog để chỉnh sửa
        bindingDialogEdit.edittextNote.setText(billItem.getNote());
        bindingDialogEdit.edittextExpense.setText(String.valueOf(billItem.getMoney()));
        //Bỏ category vô đây
        readCategoryFromLocalStorage readCategoryFromLocalStorageTask = new readCategoryFromLocalStorage(getContext(), arrayListCategory);
        readCategoryFromLocalStorageTask.execute();
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //loading the again
//                readCategoryFromLocalStorageTask.execute();
//            }
//        };
        categoryAdapter = new CategoryAdapter(this, this, arrayListCategory);
        bindingDialogEdit.gridviewCategory.setAdapter(categoryAdapter);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billItem.getDatetime());
        String datetimeString = billItem.getDatetime().getDate() + " " + makeDateString(billItem.getDatetime().getDate(),
                billItem.getDatetime().getMonth()+1, calendar.get(Calendar.YEAR));
        bindingDialogEdit.btnDatetimeDetail.setText(datetimeString);

        dialog.setContentView(viewDialogEdit);

        Window window = dialog.getWindow();
        if (window != null) {
            // Cấu hình Dialog để hiển thị full screen và mờ đằng sau
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.7f; // Giả sử bạn muốn mức độ dim là 70%
            window.setAttributes(params);
        }
        CategoryID = billItem.getCategoryID();

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
                values.put("categoryID", String.valueOf(CategoryID));
                String whereClause = DbContract.BillEntry.COLUMN_USER_ID + "=? AND " +
                        DbContract.BillEntry.COLUMN_TIMECREATE + "=?";
                String[] whereArgs = new String[]{
                        String.valueOf(billItem.getUserID()),
                        String.valueOf(billItem.getDatetime().getTime())
                };
                // Thực hiện cập nhật dữ liệu vào local db
                database.update(DbContract.BillEntry.TABLE_NAME, values, whereClause, whereArgs);
                // Sau khi cập nhật dữ liệu, đọc lại dữ liệu từ cơ sở dữ liệu và cập nhật lại ListView
//                readFromLocalStorageTask readFromLocalStorageTask = new readFromLocalStorageTask(CalendarFragment.this);
//                readFromLocalStorageTask.execute(billItem.getDateTime().getYear(),billItem.getDateTime().getMonth(),billItem.getDateTime().getDate());
                callReadFromStorageTaskByDay();
                callReadFromStorageTaskByMonth();
                //fetch data mới lên remote db
                Cursor cursor = dbHelper.getBill(billItem.getUserID(), billItem.getDatetime(), database);
                int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
                int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
                int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
                int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
                int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
                if(cursor.moveToFirst()) {
                        int userID = cursor.getInt(columnIndexUserID);
                        String categoryID = cursor.getString(columnIndexCategoryID);
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
                                            Toast.makeText(getContext(), getString(R.string.Saved), Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            //neu server tra về "fail"
                                            Toast.makeText(getContext(),getString(R.string.SavedOffline) + serverResponse,Toast.LENGTH_LONG).show();
                                            Log.d("Update response error",serverResponse);
                                            //khong lam gì cả
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    finally {
                                        cursor.close();
                                        dbHelper.close();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error appropriately (e.g., log or notify the user)
                                    Toast.makeText(getContext(),getString(R.string.SavedOffline) + error,Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("billID", String.valueOf(billID));
                                params.put("note", note);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                params.put("timecreate", dateFormat.format(finalTimeCreate));
                                params.put("expense", String.valueOf(expense));
                                params.put("categoryID", categoryID);
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
                if (checkNetworkConnection()) {
                    DbHelper dbHelper = new DbHelper(getContext());
                    SQLiteDatabase database = dbHelper.getWritableDatabase();


                    // Gửi yêu cầu xóa dữ liệu tương ứng trên server
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCBILL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String serverResponse = jsonObject.getString("response");
                                        if (serverResponse.equals("OK")) {
                                            // Xác định điều kiện WHERE để xóa dữ liệu từ local db
                                            String whereClause = DbContract.BillEntry.COLUMN_USER_ID + "=? AND " +
                                                    DbContract.BillEntry.COLUMN_TIMECREATE + "=?";
                                            String[] whereArgs = new String[]{
                                                    String.valueOf(billItem.getUserID()),
                                                    String.valueOf(billItem.getDatetime().getTime())
                                            };
                                            // Thực hiện xóa dữ liệu từ local db
                                            database.delete(DbContract.BillEntry.TABLE_NAME, whereClause, whereArgs);
                                            Toast.makeText(getContext(), getString(R.string.Deleted), Toast.LENGTH_LONG).show();
                                            callReadFromStorageTaskByDay();
                                            callReadFromStorageTaskByMonth();
                                        } else {
                                            Toast.makeText(getContext(), getString(R.string.Failed) + serverResponse, Toast.LENGTH_LONG).show();
                                            Log.d("Delete response error", serverResponse);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    finally {
                                        dbHelper.close();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), getString(R.string.Failed) + error, Toast.LENGTH_LONG).show();
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
                else
                    Toast.makeText(getContext(), getString(R.string.NoNetwork),Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    //Hàm thống kê income, expense total
    //public static -> các activity khác có thẻ dùng lại. vd biểu đồ
    public static ContentValues MoneyCalculate(long userid, int day, int month, int year, String calBy, String cat, Context context){
        ContentValues contentValues = new ContentValues();
        if (context == null) {
            // Handle the case where the context is null
            return contentValues;
        }
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
        //Toast.makeText(context,String.valueOf(FirstDayOfWeek.getDayOfMonth())+" - " +String.valueOf(FirstDayOfWeek.getMonthValue())+ " - " + FirstDayOfWeek.getYear(),Toast.LENGTH_LONG).show();

        double Income = 0, Expense = 0, Total;
        DbHelper dbHelper = new DbHelper(context); // Sửa lỗi: sử dụng requireContext() thay vì this
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

        int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
        int columnIndexCategoryId = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
        try {
            while (cursor.moveToNext()){
                //if (columnIndexMoney != -1) {
                Date DateTime = new Date(cursor.getLong(columnIndexDatetime));
                int userID = cursor.getInt(columnIndexUserID);
                double money = cursor.getDouble(columnIndexMoney);
                String categoryId = cursor.getString(columnIndexCategoryId);
                if(userID == userid)
                {
                    if(calBy!= context.getString(R.string.Week)){
                        if(year == DateTime.getYear()){
                            if(calBy!=context.getString(R.string.Year)){
                                if(month == DateTime.getMonth()){
                                    if(calBy!=context.getString(R.string.Month)){
                                        if(day == DateTime.getDate())
                                            //Xét theo ngày
                                            if (cat.equals("All")||categoryId.equals(cat)) {
                                                if (money < 0) Expense += money;
                                                else Income += money;
                                            }
                                    }
                                    else{
                                        //Xét theo tháng
                                        if (cat.equals("All")||categoryId.equals(cat)) {
                                            if (money < 0) Expense += money;
                                            else Income += money;
                                        }
                                    }
                                }
                            }
                            else{
                                //Xét theo năm
                                if (cat.equals("All")||categoryId.equals(cat)) {
                                    if (money < 0) Expense += money;
                                    else Income += money;
                                }
                            }
                        }
                    }
                    else{
                        //Xét theo tuần
                        if(isDateInWeek(DateTime.getDate(),DateTime.getMonth(),DateTime.getYear(),FirstDayOfWeek)){
                            if (cat.equals("All") || categoryId.equals(cat)) {
                                if (money < 0) Expense += money;
                                else Income += money;
                            }
                        }
                    }
                }
                //}
            }
            Total = Income + Expense;
            contentValues.put("Income", Income);
            contentValues.put("Expense", Expense);
            contentValues.put("Total", Total);
        } finally {
            // Đảm bảo đóng Cursor trong khối finally
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contentValues;
    }
    public static boolean isDateInWeek(int day, int month, int year, LocalDate weekFirst){
        LocalDate weekLast = weekFirst.plusDays(6);
        if(weekLast.getDayOfMonth() > weekFirst.getDayOfMonth()){
            if((year + 1) == weekFirst.getYear() && (month+1) == weekFirst.getMonthValue()
                    && weekFirst.getDayOfMonth()<=day && day <=weekLast.getDayOfMonth()){
                return true;
            }
            else return false;
        }
        else{
            if(weekFirst.getYear() == (year + 1) && weekFirst.getMonthValue() == (month+1)
            && weekFirst.getDayOfMonth()<=day)
            {
                return true;
            }
            else {
                if(weekLast.getYear() == (year + 1) && weekLast.getMonthValue() == (month+1)
                        && weekLast.getDayOfMonth()>=day){
                    return true;
                }
                else return false;
            }
        }
    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }
}
