package com.example.proj_moneymanager.activities.Expense;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.FragmentExpenseBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExpenseFragment extends Fragment {
    FragmentExpenseBinding binding;
    private Button monthYearText;
    private DatePickerDialog datePickerDialog;
    Date DateTime;

    String Note;
    double Expense;
    int isExpense;
    ImageButton Ibtn_Income, Ibtn_Expense;
    ArrayList<Bill> arrayListBill = new ArrayList<Bill>();
    Button Import;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpenseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // Edit category
        ImageButton imagebuttonEditCategory = binding.imagebuttonEditCategory;
        imagebuttonEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi sự kiện khi click ImageButton
                onEditCategoryButtonClick();
            }
        });


        // Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = (Button) binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());

        Ibtn_Expense = binding.imgbtnExpense;
        //Mặc định khi chuyển sang view này là Expense
        binding.textviewTypeofbill.setText("Expense");
        isExpense = -1;

        Ibtn_Income = binding.imgbtnIncome;
        Ibtn_Income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set màu cho image button
                //set text
                binding.textviewTypeofbill.setText("Income");
                //thay đổi chỉ số nhân = +1;
                isExpense = 1;
            }
        });
        Ibtn_Expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set màu cho image button
                //set text
                binding.textviewTypeofbill.setText("Expense");
                //thay đổi chỉ số nhân = -1;
                isExpense = -1;
            }
        });
        Import = (Button) binding.btnImport;

        Import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi Button được click
                ImportBill();
            }
        });
        return view;
    }
    public void onEditCategoryButtonClick (){
        EditCategoryFragment editCategoryFragment = new EditCategoryFragment();

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, editCategoryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
    public static String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    public static String makeDateString(int day, int month, int year)
    {
        return day +" "+ getMonthFormat(month) + " " + year;
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
            return "Octorber";
        if(month == 11)
            return "November";
        if(month == 12)
            return "December";

        //default should never happen
        return "JAN";
    }
    @SuppressLint("SuspiciousIndentation")
    private void ImportBill(){
        //kiểm tra tv tiền
        if(!binding.edittextTypeofbill.getText().toString().isEmpty()){
            if(binding.edittextNote.getText().toString().isEmpty()) Note = "Unnamed Bill";
                else Note = binding.edittextNote.getText().toString();
                try{
                    Expense = Double.parseDouble(binding.edittextTypeofbill.getText().toString());
                    Expense = Expense*isExpense;
                }catch (NumberFormatException e){
                    Toast.makeText(getContext(),"Please enter a valid number",Toast.LENGTH_SHORT).show();
                    return;
                }
                // Lấy data ngày
                String datetimeString = monthYearText.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                try {
                    DateTime = dateFormat.parse(datetimeString);

                    // Lấy giờ, phút và giây của hệ thống
                    Calendar currentCalendar = Calendar.getInstance();
                    int hour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = currentCalendar.get(Calendar.MINUTE);
                    int second = currentCalendar.get(Calendar.SECOND);
                    // Cập nhật giờ, phút và giây vào biến DateTime
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateTime);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, second);

                    // DateTime đã được cập nhật với giờ, phút và giây của hệ thống
                    Date updatedDateTime = calendar.getTime();
                    int CategoryID = 1;
                    long UserID = getArguments().getLong("UserID", 0);

                    // Ghi vào db
                    insertBillToServer(UserID, CategoryID, Note, updatedDateTime, Expense);
                    //set giá trị mặc định cho các textview
                    binding.edittextNote.setText("");
                    binding.edittextTypeofbill.setText("");
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Xử lý khi có lỗi chuyển đổi
                }
        }
        else Toast.makeText(getContext(),"Please enter a valid value",Toast.LENGTH_SHORT).show();
    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }
    private void readFromLocalStorage() {
        arrayListBill.clear();
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

        int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry._ID);
        int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
        int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
        int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexBillID != -1 && columnIndexUserID != -1 &&
                    columnIndexCategoryID != -1 && columnIndexMoney != -1 &&
                    columnIndexSyncStatus != -1) {

                int billID = cursor.getInt(columnIndexBillID);
                int userID = cursor.getInt(columnIndexUserID);
                int categoryID = cursor.getInt(columnIndexCategoryID);
                String note = cursor.getString(columnIndexNote);
                Date timeCreate = new Date();
                if (columnIndexDatetime != -1) {
                    long datetimeInMillis = cursor.getLong(columnIndexDatetime);
                    timeCreate = new Date(datetimeInMillis);
                }
                double money = cursor.getDouble(columnIndexMoney);
                int syncStatus = cursor.getInt(columnIndexSyncStatus);

//                 Create a new Bill object with all required parameters
                Bill bill = new Bill(billID, userID, categoryID, note, timeCreate, money, syncStatus);
                arrayListBill.add(bill);
            } else {
                // Handle the case where the column indices are not found
                // You may log an error, throw an exception, or handle it in some way
            }
        }
        cursor.close();
        dbHelper.close();
    }
    private long insertBillToLocalDatabaseFromApp(long userID, long categoryId, String note, Date timecreate, double expense, int synstatus){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long billID = dbHelper.insertBillToLocalDatabaseFromApp(userID, categoryId, note, timecreate, expense, synstatus, database);
        readFromLocalStorage();
        dbHelper.close();
        return billID;
    }
    private void    insertBillToServer(long userid, long categoryid, String note, Date timecreate, Double expense) {
        if (checkNetworkConnection()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCBILL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response= jsonObject.getString("response");
                                long billid;
                                if (Response.equals("OK")){
                                    billid = insertBillToLocalDatabaseFromApp(userid, categoryid, note, timecreate, expense, DbContract.SYNC_STATUS_OK);
                                }else {
                                    billid = insertBillToLocalDatabaseFromApp(userid, categoryid, note, timecreate, expense, DbContract.SYNC_STATUS_FAILED);
                                }
                                Toast.makeText(getContext(),"Import bill successfully", Toast.LENGTH_SHORT).show();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    long billid = insertBillToLocalDatabaseFromApp(userid, categoryid, note, timecreate, expense, DbContract.SYNC_STATUS_FAILED);
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("note", note);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    params.put("timecreate", dateFormat.format(timecreate));
                    params.put("expense", String.valueOf(expense));
                    params.put("categoryID", String.valueOf(categoryid));
                    params.put("userID", String.valueOf(userid));
                    params.put("method", "INSERT");
                    return params;
                }
            };
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
        else {
            insertBillToLocalDatabaseFromApp(userid, categoryid, note, timecreate, expense, DbContract.SYNC_STATUS_FAILED);
        }
    }
}