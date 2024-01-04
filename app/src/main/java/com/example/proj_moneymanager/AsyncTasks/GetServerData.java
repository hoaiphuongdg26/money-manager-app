package com.example.proj_moneymanager.AsyncTasks;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetServerData extends AsyncTask<Void, Void, String> {

    private Context context;
    private long UserID;

    public GetServerData(Context context, long userID) {
        this.context = context;
        this.UserID = userID;
    }

    @Override
    protected String doInBackground(Void... voids) {
        DbHelper dbHelper = new DbHelper(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DbContract.SERVER_URL_GETDATABASE + "?UserID=" + UserID,
                //Can tạo 1 url mới hoặc sửa sync.php cho việc get data
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject Response = null;
                        try {
                            Response = new JSONObject(response);
                            String serverResponse = Response.getString("response");
                            if (serverResponse.equals("OK")) {
                                // Lấy dữ liệu từ server cho bảng Bill
                                JSONArray BillData = Response.getJSONArray("billdata");
                                // Open local SQLite database
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                //Xoá hết dữ liệu trên bảng bill hiện tại
                                database.delete(DbContract.BillEntry.TABLE_NAME, null, null);

                                //Add lại dữ liệu từ remote gui ve vao local database
                                for (int i = 0; i < BillData.length(); i++) {
                                    JSONObject billItem = BillData.getJSONObject(i);
                                    // Extract data from JSON object
//                                                    int id = item.getInt("ID");
//                                                    int userID = item.getInt("UserID");
//                                                    int categoryID = item.getInt("CategoryID");
//                                                    String note = item.getString("Note");
                                    // Store data in local SQLite database
                                    ContentValues values = new ContentValues();
                                    values.put("_ID", Integer.parseInt(billItem.getString("ID")));
                                    values.put("UserID", Integer.parseInt(billItem.getString("UserID")));
                                    values.put("CategoryID", Integer.parseInt(billItem.getString("CategoryID")));
                                    values.put("Note", billItem.getString("Note"));
                                    Date DateTime = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                    try {
                                        DateTime = dateFormat.parse(billItem.getString("TimeCreate"));
                                    }
                                    catch (ParseException e){
                                        e.printStackTrace();
                                    }
                                    assert DateTime != null;
                                    values.put("TimeCreate", DateTime.getTime());
                                    values.put("Expense", Integer.parseInt(billItem.getString("Expense")));
                                    // Insert data into local SQLite database
                                    database.insert(DbContract.BillEntry.TABLE_NAME, null, values);
                                }
                                // Close the database
                                database.close();

                                // Lấy dữ liệu từ server cho bảng Category
                                JSONArray categoryData = Response.getJSONArray("categorydata");
                                database = dbHelper.getWritableDatabase();
                                database.delete(DbContract.CategoryEntry.TABLE_NAME, null, null);

                                for (int i = 0; i < categoryData.length(); i++) {
                                    JSONObject categoryItem = categoryData.getJSONObject(i);
                                    ContentValues values = new ContentValues();
                                    values.put(DbContract.CategoryEntry._ID, Integer.parseInt(categoryItem.getString("ID")));
                                    values.put(DbContract.CategoryEntry.COLUMN_NAME, categoryItem.getString("Name"));
                                    values.put(DbContract.CategoryEntry.COLUMN_ICON, categoryItem.getString("Icon"));
                                    values.put(DbContract.CategoryEntry.COLUMN_COLOR, categoryItem.getString("Color"));
                                    database.insert(DbContract.CategoryEntry.TABLE_NAME, null, values);
                                }
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
                        Toast.makeText(context.getApplicationContext(), "Fail to sync data", Toast.LENGTH_LONG);
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
