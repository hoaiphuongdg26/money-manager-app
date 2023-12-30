package com.example.proj_moneymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetServerData extends AsyncTask<Void, Void, String> {

    private Context context;

    public GetServerData(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        DbHelper dbHelper = new DbHelper(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DbContract.SERVER_URL,
                //Can tạo 1 url mới hoặc sửa sync.php cho việc get dâta
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject Response = null;
                        try {
                            Response = new JSONObject(response);
                            String serverResponse = Response.getString("response");
                            if (serverResponse.equals("OK")) {
                                JSONArray BillData = Response.getJSONArray("billdata");
                                // Open local SQLite database
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                //Xoá hết dữ liệu trên bảng bill hiện tại
                                database.delete(DbContract.BillEntry.TABLE_NAME, null, null);

                                //Add lại dữ liệu tuwf remote gui ve vao local database
                                for (int i = 0; i < BillData.length(); i++) {
                                    JSONObject billItem = BillData.getJSONObject(i);
                                    // Extract data from JSON object
//                                                    int id = item.getInt("ID");
//                                                    int userID = item.getInt("UserID");
//                                                    int categoryID = item.getInt("CategoryID");
//                                                    String note = item.getString("Note");
                                    // Extract other fields as needed...
                                    // Store data in local SQLite database
                                    ContentValues values = new ContentValues();
                                    values.put("_ID", Integer.parseInt(billItem.getString("ID")));
                                    values.put("UserID", Integer.parseInt(billItem.getString("UserID")));
                                    values.put("CategoryID", Integer.parseInt(billItem.getString("CategoryID")));
                                    values.put("Note", billItem.getString("Note"));
                                    values.put("TimeCreate", billItem.getString("TimeCreate"));
                                    values.put("Expense", Integer.parseInt(billItem.getString("Expense")));
                                    // Insert data into local SQLite database
                                    database.insert(DbContract.BillEntry.TABLE_NAME, null, values);
                                }
                                // Close the database
                                database.close();
                            } else {
                                // Handle server response other than "OK"
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error appropriately (e.g., log or notify the user)
                        //Toast.makeText(getApplicationContext(), "Fail to sync data", Toast.LENGTH_LONG);
                }
        });
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context.getApplicationContext(),"Get data completely",Toast.LENGTH_LONG).show();
    }
}
