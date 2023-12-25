package com.example.proj_moneymanager;

import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_CATEGORYID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_DATETIME;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_ID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_MONEY;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_NOTE;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_USERID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.UserInformation;
import com.example.proj_moneymanager.activities.ExpenseFragment;
import com.example.proj_moneymanager.activities.HomeFragment;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.activities.Profile.ProfileFragment;
import com.example.proj_moneymanager.activities.Statistic.StatisticFragment;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BroadcastReceiver broadcastReceiver;
    DbHelper database = Login.database;
    ArrayList<Bill> arrayListBill = new ArrayList<Bill>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.navBar.setOnItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.menu_home:
                    replaceFragment(new HomeFragment());
                    binding.textviewHeader.setText("HOME");
                    break;
                case R.id.menu_chart:
                    replaceFragment(new StatisticFragment());
                    binding.textviewHeader.setText("STATISTC");
                    break;
                case R.id.menu_money:
                    replaceFragment(new ExpenseFragment());
                    binding.textviewHeader.setText("EXPENSE");
                    break;
                case R.id.menu_calendar:
                    replaceFragment(new CalendarFragment());
                    binding.textviewHeader.setText("CALENDAR");
                    break;
                case R.id.menu_profile:
                    replaceFragment(new ProfileFragment());
                    binding.textviewHeader.setText("PROFILE");
                    break;
            }
            return true;
        });

        // Lấy dữ liệu từ Login trong intent
        Intent intent = getIntent();
        UserInformation userInformation = new UserInformation();
        int UserID = intent.getIntExtra("UserID", 0);

        // Gửi dữ liệu đến ExpenseFragment
        ExpenseFragment expenseFragment = new ExpenseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("UserID", UserID);
        expenseFragment.setArguments(bundle);

        Toast.makeText(getApplicationContext(), userInformation.getFullName(this, UserID), Toast.LENGTH_LONG).show();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };

        // Lấy dữ liệu từ server xuống local theo UserID
        fetchDataFromServerAndSaveToLocal(UserID);
        readFromLocalStorage();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void fetchDataFromServerAndSaveToLocal(int userID) {
        if (checkNetworkConnection()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Xử lý dữ liệu từ máy chủ và lưu vào cơ sở dữ liệu local
                                processServerData(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Xử lý lỗi (nếu có)
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Truyền tham số, ví dụ: "user_id"
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userID));
                    return params;
                }
            };

            // Thêm yêu cầu vào hàng đợi Volley
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private void processServerData(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            int id = jsonObject.getInt(TABLE_BILL_ID);
            int userID = jsonObject.getInt(String.valueOf(TABLE_BILL_USERID));
            int categoryId = jsonObject.getInt(String.valueOf(TABLE_BILL_CATEGORYID));
            String note = jsonObject.getString(TABLE_BILL_NOTE);
            Date datetime = (Date) jsonObject.get(TABLE_BILL_DATETIME);
            double money = jsonObject.getDouble(String.valueOf(TABLE_BILL_MONEY));

            dbHelper.saveBillToLocalDatabaseFromServer(id, userID, categoryId, note, datetime, money, DbContract.SYNC_STATUS_OK, database);
        }

        dbHelper.close();
        // Thực hiện các hành động cần thiết sau khi lưu dữ liệu vào cơ sở dữ liệu local
    }

    public void readFromLocalStorage() {
        arrayListBill.clear();
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

        int columnIndexBillID = cursor.getColumnIndex(TABLE_BILL_ID);
        int columnIndexUserID = cursor.getColumnIndex(TABLE_BILL_USERID);
        int columnIndexCategoryID = cursor.getColumnIndex(TABLE_BILL_CATEGORYID);
        int columnIndexNote = cursor.getColumnIndex(TABLE_BILL_NOTE);
        int columnIndexDatetime = cursor.getColumnIndex(TABLE_BILL_DATETIME);
        int columnIndexMoney = cursor.getColumnIndex(TABLE_BILL_MONEY);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexBillID != -1 && columnIndexUserID != -1 &&
                    columnIndexCategoryID != -1 && columnIndexMoney != -1 &&
                    columnIndexSyncStatus != -1) {

                int billID = cursor.getInt(columnIndexBillID);
                int userID = cursor.getInt(columnIndexUserID);
                int categoryID = cursor.getInt(columnIndexCategoryID);
                String note = cursor.getString(columnIndexNote);
                Date datetime = new Date();
                if (columnIndexDatetime != -1) {
                    long datetimeInMillis = cursor.getLong(columnIndexDatetime);
                    datetime = new Date(datetimeInMillis);
                }
                double money = cursor.getDouble(columnIndexMoney);
                int syncStatus = cursor.getInt(columnIndexSyncStatus);

//                 Create a new Bill object with all required parameters
                Bill bill = new Bill(billID, userID, categoryID, note, datetime, money, syncStatus);
                arrayListBill.add(bill);
            } else {
                // Handle the case where the column indices are not found
                // You may log an error, throw an exception, or handle it in some way
            }
        }

        cursor.close();
        dbHelper.close();
    }


//    public void saveToAppServer(int billID) {
//        DbHelper dbHelper = new DbHelper(this);
//        SQLiteDatabase database = dbHelper.getWritableDatabase();
//        if (checkNetworkConnection()) {
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                String serverResponse = jsonObject.getString("response");
//                                if (serverResponse.equals("OK")) {
//                                    saveBillToLocalDatabase(billID, DbContract.SYNC_STATUS_OK, database);
//                                } else {
//                                    saveBillToLocalDatabase(billID, DbContract.SYNC_STATUS_FAILED, database);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    saveBillToLocalDatabase(billID, DbContract.SYNC_STATUS_OK, database);
//                }
//            }) {
//                @Nullable
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("name", String.valueOf(billID));
//                    return params;
//                }
//            };
//            MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
//        } else {
//            saveBillToLocalDatabase(billID, DbContract.SYNC_STATUS_FAILED, database);
//        }
//    }
    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(DbContract.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
//    private void saveBillToLocalDatabase(int billID, int synstatus, SQLiteDatabase database) {
//        DbHelper dbHelper = new DbHelper(this);
//        dbHelper.saveBillToLocalDatabase(billID, TABLE_BILL_USERID, TABLE_CATEGORY_ID, TABLE_BILL_MONEY, synstatus, database);
//        readFromLocalStorage(); // Đọc lại dữ liệu từ local nếu cần
//        dbHelper.close();
//    }

}