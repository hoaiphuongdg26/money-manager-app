package com.example.proj_moneymanager.database;

import static com.example.proj_moneymanager.database.DbContract.SYNC_STATUS;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_CATEGORYID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_DATETIME;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_ID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_MONEY;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_NOTE;
import static com.example.proj_moneymanager.database.DbContract.TABLE_BILL_USERID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_EMAIL;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_FULLNAME;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_ID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_PASSWORD;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_PHONENUMBER;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_USERNAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class DbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_USERINFORMATION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USERINFORMATION + " (" +
                    TABLE_USERINFORMATION_ID + " INTEGER PRIMARY KEY," +
                    TABLE_USERINFORMATION_FULLNAME + " VARCHAR(150)," +
                    TABLE_USERINFORMATION_USERNAME + " VARCHAR(50)," +
                    TABLE_USERINFORMATION_PASSWORD + " VARCHAR(50)," +
                    TABLE_USERINFORMATION_EMAIL + " VARCHAR(50)," +
                    TABLE_USERINFORMATION_PHONENUMBER + " NUMBER," +
                    SYNC_STATUS + " INTEGER);";
    private static final String CREATE_TABLE_BILL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BILL + " (" +
                    TABLE_BILL_ID + " INTEGER PRIMARY KEY," +
                    TABLE_BILL_USERID + " INTEGER," +
                    TABLE_BILL_CATEGORYID + " INTEGER," +
                    TABLE_BILL_NOTE + " VARCHAR(150)," +
                    TABLE_BILL_DATETIME + " DATETIME," +
                    TABLE_BILL_MONEY + " DOUBLE," +
                    SYNC_STATUS + " INTEGER);";

    private static final String DROP_TABLE_USERINFORMATION = "DROP TABLE IF EXISTS "+ TABLE_USERINFORMATION;
    private static final String DROP_TABLE_BILL = "DROP TABLE IF EXISTS "+DbContract.TABLE_BILL;
    public DbHelper(Context context){
        super(context, DbContract.DATABASE_NAME,null,DATABASE_VERSION);
    }
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //Truy vấn k trả kết quả Create, Insert, Update, Delete,...
    public void QueryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    //Truy vấn trả kết quả
    public Cursor GetData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USERINFORMATION);
//            db.execSQL(DROP_TABLE_BILL);
            db.execSQL(CREATE_TABLE_BILL);
        } catch (SQLException e) {
            // Handle the exception, for example, log it or show a message
            Log.e("DbHelper", "Error creating USERINFOMATION table: " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USERINFORMATION);
        db.execSQL(DROP_TABLE_BILL);
        onCreate(db);
    }
    public Cursor readBillFromLocalDatabase(SQLiteDatabase database) {
        String query = "SELECT " +
                TABLE_BILL + "." + TABLE_BILL_ID + ", " +
                TABLE_BILL_USERID + ", " +
                TABLE_BILL_CATEGORYID + ", " +
                TABLE_BILL_NOTE + ", " +
                TABLE_BILL_DATETIME + ", "+
                TABLE_BILL_MONEY + ", " +
                SYNC_STATUS +
                " FROM " + TABLE_BILL;

        return database.rawQuery(query, null);
    }


    public void updateBillLocalDatabase(int billID, int syncStatus, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNC_STATUS, syncStatus);

        String selection = TABLE_BILL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(billID)};

        database.update(TABLE_BILL, contentValues, selection, selectionArgs);
    }

    public void saveBillToLocalDatabaseFromInput(int userID, int categoryId, String note, Date datetime, double money, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(TABLE_BILL_USERID, userID);
        values.put(TABLE_BILL_CATEGORYID, categoryId);
        values.put(TABLE_BILL_NOTE, note);
        values.put(TABLE_BILL_DATETIME, datetime.getTime());
        values.put(TABLE_BILL_MONEY, money);

        // Thêm dữ liệu vào bảng BILL
        values.put(DbContract.SYNC_STATUS, synstatus);
        database.insert(TABLE_BILL, null, values);
    }
    public void saveBillToLocalDatabaseFromServer(int id, int userID, int categoryId, String note, Date datetime, double money, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(TABLE_BILL_ID, id);
        values.put(TABLE_BILL_USERID, userID);
        values.put(TABLE_BILL_CATEGORYID, categoryId);
        values.put(TABLE_BILL_NOTE, note);
        values.put(TABLE_BILL_DATETIME, datetime.getTime());
        values.put(TABLE_BILL_MONEY, money);

        // Thêm dữ liệu vào bảng BILL
        values.put(DbContract.SYNC_STATUS, synstatus);
        database.insert(TABLE_BILL, null, values);
    }
    public void saveUserToLocalDatabase(String userID, String FullName, String UserName, String Password, String Email, String PhoneNumber, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(TABLE_USERINFORMATION_ID, userID);
        values.put(TABLE_USERINFORMATION_FULLNAME, FullName);
        values.put(TABLE_USERINFORMATION_USERNAME, UserName);
        values.put(TABLE_USERINFORMATION_PASSWORD, Password);
        values.put(TABLE_USERINFORMATION_EMAIL, Email);
        values.put(TABLE_USERINFORMATION_PHONENUMBER, PhoneNumber);

        // Thêm dữ liệu vào bảng BILL
        values.put(SYNC_STATUS, synstatus);
        database.insertWithOnConflict(TABLE_USERINFORMATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
