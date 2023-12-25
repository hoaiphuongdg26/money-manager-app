package com.example.proj_moneymanager;

import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_ID;

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
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkNetworkConnection(context)) {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

            while (cursor.moveToNext()) {
                int syncStatus = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
                if (syncStatus == DbContract.SYNC_STATUS_FAILED) {
                    int billID = cursor.getInt(cursor.getColumnIndex(TABLE_BILL_ID));

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String serverResponse = jsonObject.getString("response");
                                        if (serverResponse.equals("OK")) {
                                            dbHelper.updateBillLocalDatabase(billID, DbContract.SYNC_STATUS_OK, database);
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
                            params.put("billID", String.valueOf(billID));
                            return params;
                        }
                    };

                    MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                }
            }

            dbHelper.close();
        }
    }

    public boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
