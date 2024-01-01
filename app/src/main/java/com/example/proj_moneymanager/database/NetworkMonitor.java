package com.example.proj_moneymanager.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
        if (checkNetworkConnection(context)) {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

            int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry._ID);
            int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
            int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
            int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
            int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
            int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
            int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);
            while (cursor.moveToNext()) {
                int syncStatus = cursor.getInt(columnIndexSyncStatus);
                if (syncStatus == DbContract.SYNC_STATUS_FAILED) {
                    int billID = cursor.getInt(columnIndexBillID);
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

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String serverResponse = jsonObject.getString("response");
                                        long billid;
                                        if (serverResponse.equals("OK")) {
                                            billid = dbHelper.insertBillToLocalDatabaseFromApp(userID, categoryID, note, finalTimeCreate, expense, DbContract.SYNC_STATUS_OK, database);
                                            context.sendBroadcast(new Intent(DbContract.UI_UPDATE_BROADCAST));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error appropriately (e.g., log or notify the user)
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

            dbHelper.close();
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
