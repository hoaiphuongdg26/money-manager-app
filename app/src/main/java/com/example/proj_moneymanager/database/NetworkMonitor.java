package com.example.proj_moneymanager.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NetworkMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (checkNetworkConnection(context)) {
                Toast.makeText(context.getApplicationContext(), "Internet connected", Toast.LENGTH_LONG);
                DbHelper dbHelper = new DbHelper(context);
                SQLiteDatabase database = dbHelper.getReadableDatabase();

                // TABLE BILL
                Cursor billCursor = dbHelper.readBillFromLocalDatabase(database);
                int columnIndexID_BILL = billCursor.getColumnIndex(DbContract.BillEntry._ID);
                int columnIndexUserID_BILL = billCursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
                int columnIndexCategoryID_BILL = billCursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
                int columnIndexNote_BILL = billCursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
                int columnIndexDatetime_BILL = billCursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
                int columnIndexMoney_BILL = billCursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
                int columnIndexSyncStatus_BILL = billCursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

                while (billCursor.moveToNext()) {
                    int syncStatus_BILL = billCursor.getInt(columnIndexSyncStatus_BILL);
                    if (syncStatus_BILL == DbContract.SYNC_STATUS_FAILED) {
                        int billID = billCursor.getInt(columnIndexID_BILL);
                        int userID = billCursor.getInt(columnIndexUserID_BILL);
                        int categoryID = billCursor.getInt(columnIndexCategoryID_BILL);
                        String note = billCursor.getString(columnIndexNote_BILL);
                        Date timeCreate = new Date();
                        if (columnIndexDatetime_BILL != -1) {
                            long datetimeInMillis = billCursor.getLong(columnIndexDatetime_BILL);
                            timeCreate = new Date(datetimeInMillis);
                        }
                        Date finalTimeCreate = timeCreate;

                        double expense = billCursor.getDouble(columnIndexMoney_BILL);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCBILL,
                                new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String serverResponse = jsonObject.getString("response");
                                        if (serverResponse.equals("OK")) {
                                            dbHelper.updateBillInLocalDatabase(billID, userID, categoryID, note, finalTimeCreate, expense, DbContract.SYNC_STATUS_OK, database);
                                            context.sendBroadcast(new Intent(DbContract.UI_UPDATE_BROADCAST));
                                        }
                                        else{
                                            //neu server tra về "fail"
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
                                Toast.makeText(context.getApplicationContext(), "Fail to sync data", Toast.LENGTH_LONG);
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
                                return params;
                            }
                        };

                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                    }
                }
                //dbHelper.close();

                //TABLE CATEGORY
                Cursor categoryCursor = dbHelper.readBillFromLocalDatabase(database);
                int columnIndexID_CATEGORY = categoryCursor.getColumnIndex(DbContract.CategoryEntry._ID);
                int columnIndexName_CATEGORY = categoryCursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_NAME);
//        int columnIndexIcon_CATEGORY = categoryCursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_ICON);
                int columnIndexColor_CATEGORY = categoryCursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_COLOR);
                int columnIndexSyncStatus_CATEGORY = categoryCursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_SYNC_STATUS);
                while (categoryCursor.moveToNext()) {
                    int syncStatus_CATEGORY = categoryCursor.getInt(columnIndexSyncStatus_CATEGORY);
                    if (syncStatus_CATEGORY == DbContract.SYNC_STATUS_FAILED) {
                        int categoryID = categoryCursor.getInt(columnIndexID_CATEGORY);
                        String name = categoryCursor.getString(columnIndexName_CATEGORY);
//                        int icon = categoryCursor.getInt(columnIndexIcon_CATEGORY);
                        String color = categoryCursor.getString(columnIndexColor_CATEGORY);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCCATEGORY,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String serverResponse = jsonObject.getString("response");
                                            if (serverResponse.equals("OK")) {
                                                dbHelper.updateCategoryInLocalDatabase(categoryID, name, color, DbContract.SYNC_STATUS_OK, database);
                                                context.sendBroadcast(new Intent(DbContract.UI_UPDATE_BROADCAST));
                                            }
                                            else{
                                                //neu server tra về "fail"
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
                                Toast.makeText(context.getApplicationContext(), "Fail to sync data", Toast.LENGTH_LONG);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", name);
//                                params.put("icon", icon);
                                params.put("color", String.valueOf(color));
                                return params;
                            }
                        };

                        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                    }
                }
            } else {
                Toast.makeText(context.getApplicationContext(), "Internet disconnected", Toast.LENGTH_LONG);
            }
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
