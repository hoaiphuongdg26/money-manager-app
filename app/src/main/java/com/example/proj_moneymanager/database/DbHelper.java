package com.example.proj_moneymanager.database;

import static com.example.proj_moneymanager.database.DbContract.BillEntry;
import static com.example.proj_moneymanager.database.DbContract.UserInformationEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USERINFORMATION =
            "CREATE TABLE IF NOT EXISTS " + UserInformationEntry.TABLE_NAME + " (" +
                    UserInformationEntry._ID + " INTEGER PRIMARY KEY," +
                    UserInformationEntry.COLUMN_FULL_NAME + " VARCHAR(150)," +
                    UserInformationEntry.COLUMN_USERNAME + " VARCHAR(50)," +
                    UserInformationEntry.COLUMN_PASSWORD + " VARCHAR(50)," +
                    UserInformationEntry.COLUMN_EMAIL + " VARCHAR(50)," +
                    UserInformationEntry.COLUMN_PHONE_NUMBER + " NUMBER," +
                    UserInformationEntry.COLUMN_SYNC_STATUS + " INTEGER);";

    private static final String CREATE_TABLE_BILL =
            "CREATE TABLE IF NOT EXISTS " + BillEntry.TABLE_NAME + " (" +
                    BillEntry._ID + " INTEGER PRIMARY KEY," +
                    BillEntry.COLUMN_USER_ID + " INTEGER," +
                    BillEntry.COLUMN_CATEGORY_ID + " INTEGER," +
                    BillEntry.COLUMN_NOTE + " VARCHAR(150)," +
                    BillEntry.COLUMN_TIMECREATE + " DATETIME," +
                    BillEntry.COLUMN_EXPENSE + " DOUBLE," +
                    BillEntry.COLUMN_SYNC_STATUS + " INTEGER);";
    private static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS " + DbContract.CategoryEntry.TABLE_NAME + " (" +
                    DbContract.CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                    DbContract.CategoryEntry.COLUMN_NAME + " VARCHAR(150)," +
                    DbContract.CategoryEntry.COLUMN_ICON + " VARCHAR(150)," +
                    DbContract.CategoryEntry.COLUMN_COLOR + " VARCHAR(150)," +
                    DbContract.CategoryEntry.COLUMN_SYNC_STATUS + " INTEGER);";
    private static final String DROP_TABLE_USERINFORMATION = "DROP TABLE IF EXISTS " + UserInformationEntry.TABLE_NAME;
    private static final String DROP_TABLE_BILL = "DROP TABLE IF EXISTS " + BillEntry.TABLE_NAME;
    private static final String DROP_TABLE_CATEGORY = "DROP TABLE IF EXISTS " + DbContract.CategoryEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
//            db.execSQL(DROP_TABLE_BILL);
//            db.execSQL(DROP_TABLE_USERINFORMATION);
//            db.execSQL(DROP_TABLE_CATEGORY);
            db.execSQL(CREATE_TABLE_USERINFORMATION);
            db.execSQL(CREATE_TABLE_BILL);
            db.execSQL(CREATE_TABLE_CATEGORY);
        } catch (SQLException e) {
            Log.e("DbHelper", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USERINFORMATION);
        db.execSQL(DROP_TABLE_BILL);
        db.execSQL(DROP_TABLE_CATEGORY);
        onCreate(db);
    }
    public void insertUserToLocalDatabase(String userID, String fullName, String userName, String password, String email, String phoneNumber, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(UserInformationEntry._ID, userID);
        values.put(UserInformationEntry.COLUMN_FULL_NAME, fullName);
        values.put(UserInformationEntry.COLUMN_USERNAME, userName);
        values.put(UserInformationEntry.COLUMN_PASSWORD, password);
        values.put(UserInformationEntry.COLUMN_EMAIL, email);
        values.put(UserInformationEntry.COLUMN_PHONE_NUMBER, phoneNumber);

        values.put(UserInformationEntry.COLUMN_SYNC_STATUS, synstatus);
        database.insertWithOnConflict(UserInformationEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // TABLE BILL
    public Cursor readBillFromLocalDatabase(SQLiteDatabase database) {
        String query = "SELECT " +
                BillEntry.TABLE_NAME + "." + BillEntry._ID + ", " +
                BillEntry.COLUMN_USER_ID + ", " +
                BillEntry.COLUMN_CATEGORY_ID + ", " +
                BillEntry.COLUMN_NOTE + ", " +
                BillEntry.COLUMN_TIMECREATE + ", " +
                BillEntry.COLUMN_EXPENSE + ", " +
                BillEntry.COLUMN_SYNC_STATUS +
                " FROM " + BillEntry.TABLE_NAME;

        return database.rawQuery(query, null);
    }

    public long insertBillToLocalDatabaseFromApp(long userID, long categoryId, String note, Date timecreate, double money, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(BillEntry.COLUMN_USER_ID, userID);
        values.put(BillEntry.COLUMN_CATEGORY_ID, categoryId);
        values.put(BillEntry.COLUMN_NOTE, note);
        values.put(BillEntry.COLUMN_TIMECREATE, timecreate.getTime());
        values.put(BillEntry.COLUMN_EXPENSE, money);
        values.put(BillEntry.COLUMN_SYNC_STATUS, synstatus);

        // Insert dữ liệu và trả về ID của hàng vừa thêm
        return database.insert(BillEntry.TABLE_NAME, null, values);
    }

    public void updateBillInLocalDatabase(int id, int userID, int categoryId, String note, Date datetime, double money, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(BillEntry._ID, id);
//        values.put(BillEntry.COLUMN_USER_ID, userID);
//        values.put(BillEntry.COLUMN_CATEGORY_ID, categoryId);
//        values.put(BillEntry.COLUMN_NOTE, note);
//        values.put(BillEntry.COLUMN_TIMECREATE, datetime.getTime());
//        values.put(BillEntry.COLUMN_EXPENSE, money);
//
        values.put(BillEntry.COLUMN_SYNC_STATUS, synstatus);

        //mới sửa
        String selection = BillEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        database.update(BillEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public Cursor getBill(long userID, Date timecreate, SQLiteDatabase database) {
        String[] projection = {
                BillEntry.COLUMN_NOTE,
                BillEntry.COLUMN_TIMECREATE,
                BillEntry.COLUMN_EXPENSE,
                BillEntry.COLUMN_CATEGORY_ID,
                BillEntry.COLUMN_USER_ID
        };

        String whereClause = DbContract.BillEntry.COLUMN_USER_ID + "=? AND " +
                DbContract.BillEntry.COLUMN_TIMECREATE + "=?";
        String[] whereArgs = new String[]{
                String.valueOf(userID),
                String.valueOf(timecreate.getTime())
        };

        return database.query(
                BillEntry.TABLE_NAME,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }
    public Cursor getBillById(long billID, SQLiteDatabase database) {
        String[] projection = {
                BillEntry.COLUMN_NOTE,
                BillEntry.COLUMN_TIMECREATE,
                BillEntry.COLUMN_EXPENSE,
                BillEntry.COLUMN_CATEGORY_ID,
                BillEntry.COLUMN_USER_ID
        };

        String selection = BillEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(billID)};

        return database.query(
                BillEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
    public long insertCategoryToLocalDatabaseFromApp(String name, String icon, String color, int syncstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DbContract.CategoryEntry.COLUMN_NAME, name);
        values.put(DbContract.CategoryEntry.COLUMN_ICON, icon);
        values.put(DbContract.CategoryEntry.COLUMN_COLOR, color);
        values.put(DbContract.CategoryEntry.COLUMN_SYNC_STATUS, syncstatus);

        // Insert dữ liệu và trả về ID của hàng vừa thêm
        return database.insert(DbContract.CategoryEntry.TABLE_NAME, null, values);
    }
    public Cursor readCategoryFromLocalDatabase(SQLiteDatabase database) {
        String query = "SELECT " +
                DbContract.CategoryEntry._ID + ", " +
                DbContract.CategoryEntry.COLUMN_NAME + ", " +
                DbContract.CategoryEntry.COLUMN_ICON + ", " +
                DbContract.CategoryEntry.COLUMN_COLOR + ", " +
                DbContract.CategoryEntry.COLUMN_SYNC_STATUS +
                " FROM " + DbContract.CategoryEntry.TABLE_NAME;

        return database.rawQuery(query, null);
    }
    public void updateCategoryInLocalDatabase(int id, String name, String icon, String color, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DbContract.CategoryEntry._ID, id);
        values.put(DbContract.CategoryEntry.COLUMN_SYNC_STATUS, synstatus);

        String selection = DbContract.CategoryEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        database.update(DbContract.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public boolean isCategoryNameExists(String name) {
        SQLiteDatabase database = getReadableDatabase();

        // Truy vấn kiểm tra xem đã tồn tại name trong cơ sở dữ liệu hay chưa
        String query = "SELECT * FROM " + DbContract.CategoryEntry.TABLE_NAME +
                " WHERE " + DbContract.CategoryEntry.COLUMN_NAME + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{name});

        boolean exists = cursor.getCount() > 0;

        // Đóng cursor sau khi kiểm tra xong
        cursor.close();

        return exists;
    }
    public String getCategoryNameById(long categoryId, SQLiteDatabase database) {
        String[] projection = {
                DbContract.CategoryEntry.COLUMN_NAME
        };

        String selection = DbContract.CategoryEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};

        Cursor cursor = database.query(
                DbContract.CategoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String categoryName = "";

        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_NAME));
        }

        cursor.close();
        return categoryName;
    }


}
