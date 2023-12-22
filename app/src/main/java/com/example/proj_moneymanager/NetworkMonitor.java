package com.example.proj_moneymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.api.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkNetworkConnection(context)){
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor cursor = dbHelper.readFromLocalDatabase(database);
            while (cursor.moveToNext()){
                int sync_status = cursor.getInt(cursor.getColumnIndex((DbContract.SYNC_STATUS)));
                if (sync_status = DbContract.SYNC_STATUS_FAILED){
                    String Name = cursor.getString(Cursor.getColumnIndex(DbContract.NAME));
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL,
                            new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response){
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String Response = jsonObject.getString("response");
                                        if (Response.equals("OK")) {
                                            dbHelper.updateLocalDatabase(Name, DbContract.SYNC_STATUS_OK,database);
                                            context.sendBroadcast(new Intent((DbContract.UI_UPDATE_BROADCAST)));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailError{
                            Map<String, String> params =new HashMap<>();
                            params.put("name",Name);
                            return params;
                            return  super.getParams();
                        }
                    };
                    MySingleton.getInstance(context).addToRequestQue(stringRequest);

                }
            }
            dbHelper.close();
        }
    }
    public boolean checkNetworkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
