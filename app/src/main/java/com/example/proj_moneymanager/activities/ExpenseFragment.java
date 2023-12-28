package com.example.proj_moneymanager.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {
    FragmentExpenseBinding binding;
    private Button monthYearText;
    private DatePickerDialog datePickerDialog;
    Date DateTime;

    String Note;
    double Expense;
    ImageButton Import;
    ArrayList<Bill> arrayListBill = new ArrayList<Bill>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        binding = FragmentExpenseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = (Button) binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());

        Import = (ImageButton) binding.btnImport;
        Import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi Button được click
                ImportBill();
            }
        });
        return view;
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
        return day +" "+ getMonthFormat(month) + " " + year;
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
    private void ImportBill(){
        // Lấy data ngày
        String datetimeString = monthYearText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        try {
            DateTime = dateFormat.parse(datetimeString);
            Note = binding.edittextNote.getText().toString();
            Expense = Double.parseDouble(binding.edittextExpense.getText().toString());
            int CategoryID = 1;
            long UserID = getArguments().getLong("UserID", 0);

            // Ghi vào db
            insertBillToServer(UserID, CategoryID, Note, DateTime, Expense);

        } catch (ParseException e) {
            e.printStackTrace();
            // Xử lý khi có lỗi chuyển đổi
        }
    }
    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    public void readFromLocalStorage() {
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
    private void insertBillToServer(long userid, long categoryid, String note, Date timecreate, Double expense) {
        if (checkNetworkConnection()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL,
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