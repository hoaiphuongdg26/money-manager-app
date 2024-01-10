package com.example.proj_moneymanager.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.activities.Statistic.StatisticFragment;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;

import java.util.ArrayList;
import java.util.Date;

public class readBillFromLocalStorage extends AsyncTask<Integer, Void, ArrayList<Bill>> {
    private ArrayList<Bill> arrayListBill;
    private Context context;
    // Constructor nhận danh sách category từ bên ngoài
    public readBillFromLocalStorage(Context context, ArrayList<Bill> arrayListBill) {
        this.context =context;
        this.arrayListBill = arrayListBill;
    }
    @Override
    protected void onPostExecute(ArrayList<Bill> arrResult) {
        arrResult = arrayListBill;
        super.onPostExecute(arrResult);
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ArrayList<Bill> doInBackground(Integer... params) {
        arrayListBill.clear(); // Xóa dữ liệu hiện tại để cập nhật từ đầu

        String billID;
        long userID;
        DbHelper dbHelper = new DbHelper(context); // Sửa lỗi: sử dụng requireContext() thay vì this
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readBillFromLocalDatabase(database);

        int columnIndexBillID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_ID);
        int columnIndexUserID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_USER_ID);
        int columnIndexCategoryID = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_CATEGORY_ID);
        int columnIndexNote = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_NOTE);
        int columnIndexDatetime = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_TIMECREATE);
        int columnIndexMoney = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_EXPENSE);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.BillEntry.COLUMN_SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexNote != -1 && columnIndexMoney != -1) {
                Date DateTime = new Date(cursor.getLong(columnIndexDatetime));
                billID = cursor.getString(columnIndexBillID);
                userID = cursor.getLong(    columnIndexUserID);
                String categoryID = cursor.getString(columnIndexCategoryID);
                long money = cursor.getLong(columnIndexMoney);
                String note = cursor.getString(columnIndexNote);
                int sync = cursor.getInt(columnIndexSyncStatus);
                if(params[0]!=-1){
                    if(DateTime.getYear() == params[0]){
                        if(params[1]!=-1){
                            if(DateTime.getMonth() == params[1]){
                                if(params[2]!=-1){
                                    if(DateTime.getDate()==params[2]){
                                        //Tính theo ngày
                                        // Tạo đối tượng Bill từ dữ liệu cơ sở dữ liệu
                                        Bill bill = new Bill(billID, userID,categoryID, note,  DateTime, money, sync);
                                        // Thêm vào danh sách
                                        arrayListBill.add(bill);
                                    }
                                } else {
                                    //Tính theo tháng
                                    Bill bill = new Bill(billID, userID,categoryID, note,  DateTime, money, sync);
                                    arrayListBill.add(bill);
                                }
                            }
                        } else{
                            //Tính theo năm
                            Bill bill = new Bill(billID, userID,categoryID, note,  DateTime, money, sync);
                            arrayListBill.add(bill);
                        }
                    }
                } else {
                    //lấy tất cả
                    Bill bill = new Bill(billID, userID,categoryID, note,  DateTime, money, sync);
                    arrayListBill.add(bill);
                }
            } else {
                // Handle the case where the column indices are not found
            }
        }
        cursor.close();
        dbHelper.close();
        return arrayListBill;
    }
}

