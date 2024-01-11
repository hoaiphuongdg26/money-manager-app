package com.example.proj_moneymanager.activities.Expense;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.AsyncTasks.readBillFromLocalStorage;
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExpenseFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener  {
    FragmentExpenseBinding binding;
    private Button monthYearText;
    private DatePickerDialog datePickerDialog;
    Date DateTime;
    String Note;
    double Expense;
    Button Import;
    long UserID;
    String CategoryID, billID;
    int isExpense;
    Button Ibtn_Income, Ibtn_Expense;
    ImageButton btnNextDay, btnPreviousDay;
    ArrayList<Bill> arrayListBill = new ArrayList<Bill>();
    ArrayList<Category> arrayListCategory = new ArrayList<Category>();
    private CategoryAdapter categoryAdapter;
    private BroadcastReceiver broadcastReceiver;
    private LocalDate selectedDate;
    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UserID = getArguments().getLong("UserID", 0);
        binding = FragmentExpenseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        // New category
        ImageButton imagebuttonNewCategory = binding.imagebuttonNewCategory;
        imagebuttonNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi sự kiện khi click ImageButton
                onNewCategoryButtonClick();
            }
        });

        // Edit category
        ImageButton imageButtonEditCategory = binding.imagebuttonEditCategory;
        imageButtonEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditCategoryButtonClick();
            }
        });


        readCategoryFromLocalStorage readCategoryFromLocalStorageTask = new readCategoryFromLocalStorage(getContext(), arrayListCategory);
        readCategoryFromLocalStorageTask.execute();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //loading the again
                readCategoryFromLocalStorageTask.execute();
            }
        };
        categoryAdapter = new CategoryAdapter(this, this, arrayListCategory);
        GridView gridView = binding.gridviewCategory;
        gridView.setAdapter(categoryAdapter);

        selectedDate = LocalDate.now();
        // Xử lý chọn tháng nhanh
        initDatePicker(view);
        monthYearText = (Button) binding.btnDatetimeDetail;
        monthYearText.setText(getTodaysDate());


        btnNextDay = binding.btnNextDay;
        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                nextDayAction(v);
            }
        });
        btnPreviousDay = binding.btnPreviousDay;
        btnPreviousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to handle the previous month action
                previousDayAction(v);
            }
        });

        Ibtn_Expense = binding.imgbtnExpense;
        //Mặc định khi chuyển sang view này là Expense
        binding.textviewTypeofbill.setText(getString(R.string.Expense));
        isExpense = -1;

        Ibtn_Income = binding.imgbtnIncome;
        Ibtn_Income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set màu cho image button
                //set text
                binding.textviewTypeofbill.setText(getString(R.string.Income));
                //thay đổi chỉ số nhân = +1;
                isExpense = 1;
            }
        });
        Ibtn_Expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set màu cho image button
                //set text
                binding.textviewTypeofbill.setText(getString(R.string.Expense));
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
    public void onNewCategoryButtonClick (){
        Bundle args = new Bundle();
        args.putLong("UserID", UserID);

        NewCategoryFragment newCategoryFragment = new NewCategoryFragment();
        newCategoryFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, newCategoryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    public void onEditCategoryButtonClick (){
        Bundle args = new Bundle();
        args.putLong("UserID", UserID);

        EditCategoryFragment editCategoryFragment = new EditCategoryFragment();
        editCategoryFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, editCategoryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    public void previousDayAction(View view) {
        selectedDate = selectedDate.minusDays(1);
        updateMonthYearText(selectedDate.getDayOfMonth(), selectedDate.getMonthValue() - 1, selectedDate.getYear());
    }

    public void nextDayAction(View view) {
        selectedDate = selectedDate.plusDays(1);
        updateMonthYearText(selectedDate.getDayOfMonth(), selectedDate.getMonthValue() - 1, selectedDate.getYear());
    }
    private void updateMonthYearText(int day, int month, int year) {
        String date = makeDateString(day, month + 1, year);
        monthYearText.setText(date);
    }
    private void initDatePicker(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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

        // Update the monthYearText here
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
        return "ERROR";
    }
    @SuppressLint("SuspiciousIndentation")
    private void ImportBill(){
        //kiểm tra tv tiền
        if(!binding.edittextTypeofbill.getText().toString().isEmpty()){
            if(binding.edittextNote.getText().toString().isEmpty()) Note = "Unnamed Bill";
            else Note = binding.edittextNote.getText().toString();

            // Kiểm tra CategoryID
            if (CategoryID == null || CategoryID.isEmpty()) {
                Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Expense = Double.parseDouble(binding.edittextTypeofbill.getText().toString());
                Expense = Expense*isExpense;
            }catch (NumberFormatException e){
                Toast.makeText(getContext(),getString(R.string.Please_enter_a_valid_number),Toast.LENGTH_SHORT).show();
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
        else Toast.makeText(getContext(),getString(R.string.Please_enter_a_valid_value),Toast.LENGTH_SHORT).show();
    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }
    private String insertBillToLocalDatabaseFromApp(long userID, String categoryId, String note, Date timecreate, double expense, int synstatus){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String billID = dbHelper.insertBillToLocalDatabaseFromApp(userID, categoryId, note, timecreate, expense, synstatus, database);

        readBillFromLocalStorage readBillFromLocalStorage = new readBillFromLocalStorage(getContext(),arrayListBill);
        readBillFromLocalStorage.execute(-1,-1,-1);

        dbHelper.close();
        return billID;
    }
    private void insertBillToServer(long userid, String categoryid, String note, Date timecreate, Double expense) {
        if (checkNetworkConnection()){
            billID = insertBillToLocalDatabaseFromApp(userid, categoryid, note, timecreate, expense, DbContract.SYNC_STATUS_PENDING);
            DbHelper dbHelper = new DbHelper(getContext());
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCBILL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response= jsonObject.getString("response");
                                if (Response.equals("OK")){
                                    dbHelper.updateBillInLocalDatabase(billID, DbContract.SYNC_STATUS_OK, database);
                                }else {
                                    dbHelper.updateBillInLocalDatabase(billID, DbContract.SYNC_STATUS_FAILED, database);
                                }
                                Toast.makeText(getContext(),getString(R.string.Import_bill_successfully), Toast.LENGTH_SHORT).show();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            finally {
                                dbHelper.close();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "Error occurred during import Bill";
                    dbHelper.updateBillInLocalDatabase(billID, DbContract.SYNC_STATUS_FAILED, database);
                    if (error != null && error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    dbHelper.close();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("billID", billID);
                    params.put("userID", String.valueOf(userid));
                    params.put("categoryID", categoryid);
                    params.put("note", note);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    params.put("timecreate", dateFormat.format(timecreate));
                    params.put("expense", String.valueOf(expense));
                    params.put("method", "INSERT");
                    return params;
                }
            };
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
        else {
            billID = insertBillToLocalDatabaseFromApp(userid, categoryid, note, timecreate, expense, DbContract.SYNC_STATUS_FAILED);
            Toast.makeText(getContext(), "No network connection. Import Bill failed.", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onCategoryClick(String selectedCategoryId) {
        CategoryID = selectedCategoryId;
        Toast.makeText(getContext(), "CategoryID: " +String.valueOf(selectedCategoryId), Toast.LENGTH_LONG).show();
    }
}