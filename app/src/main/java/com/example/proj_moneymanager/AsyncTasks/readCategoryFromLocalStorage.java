package com.example.proj_moneymanager.AsyncTasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;

import java.util.ArrayList;

public class readCategoryFromLocalStorage extends AsyncTask<Void, Void, String> {
    private ArrayList<Category> arrayListCategory;
    private Context context;
    // Constructor nhận danh sách category từ bên ngoài
    public readCategoryFromLocalStorage(Context context, ArrayList<Category> arrayListCategory) {
        this.context =context;
        this.arrayListCategory = arrayListCategory;
    }
    @Override
    protected String doInBackground(Void... voids) {
        if (arrayListCategory == null) {
            arrayListCategory = new ArrayList<>();
        } else {
            arrayListCategory.clear();
        }
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readCategoryFromLocalDatabase(database);

        int columnIndexCategoryID = cursor.getColumnIndex(DbContract.CategoryEntry._ID);
        int columnIndexName = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_NAME);
        int columnIndexIcon = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_ICON);
        int columnIndexColor = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_COLOR);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexCategoryID != -1 && columnIndexName != -1 &&
                    columnIndexColor != -1 && columnIndexIcon != -1 &&
                    columnIndexSyncStatus != -1) {

                int categoryID = cursor.getInt(columnIndexCategoryID);
                String name = cursor.getString(columnIndexName);
                String icon = cursor.getString(columnIndexIcon);
                String color = cursor.getString(columnIndexColor);
                int syncStatus = cursor.getInt(columnIndexSyncStatus);

//                 Create a new Category object with all required parameters
                Category category = new Category(categoryID, name, icon, color, syncStatus);
                arrayListCategory.add(category);
            } else {
                // Handle the case where the column indices are not found
                // You may log an error, throw an exception, or handle it in some way
            }
        }
        cursor.close();
        dbHelper.close();
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}