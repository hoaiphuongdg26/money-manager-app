package com.example.proj_moneymanager.AsyncTasks;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;
import com.example.proj_moneymanager.activities.Plan.HistoryAdapter;
import com.example.proj_moneymanager.activities.Plan.History_Option;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;

import java.util.ArrayList;
import java.util.Date;

public class readBillFromLocalStorage extends AsyncTask<Void, Void, String> {
    private ArrayList<Bill> arrayListBill;
    private Context context;
    // Constructor nhận danh sách category từ bên ngoài
    public readBillFromLocalStorage(Context context, ArrayList<Bill> arrayListBill) {
        this.context =context;
        this.arrayListBill = arrayListBill;
    }
    @Override
    protected void onPostExecute(ArrayList<History_Option> arrResult) {
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getContext(), "Read data completely", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Void... voids) {
        arrayListBill.clear(); // Xóa dữ liệu hiện tại để cập nhật từ đầu

        DbHelper dbHelper = new DbHelper(context); // Sửa lỗi: sử dụng requireContext() thay vì this
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

        //int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry._ID);
        int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
        //int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
        int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexNote != -1 && columnIndexMoney != -1) {
                Date DateTime = new Date(cursor.getLong(columnIndexDatetime));
                int userID = cursor.getInt(columnIndexUserID);
//                int categoryID = cursor.getInt(columnIndexCategoryID);
                double money = cursor.getDouble(columnIndexMoney);
                String note = cursor.getString(columnIndexNote);
                int sync = cursor.getInt(columnIndexSyncStatus);

                // Tạo đối tượng History_Option từ dữ liệu cơ sở dữ liệu
                Bill bill = new History_Option(DateTime, userID,"Test", note, R.drawable.btn_food, String.valueOf(money), sync);
                // Thêm vào danh sách
                arrayListBill.add(bill);
            } else {
                // Handle the case where the column indices are not found
            }
        }
        return arrayListBill;
    }
}

