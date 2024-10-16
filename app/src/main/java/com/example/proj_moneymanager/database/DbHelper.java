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

import com.example.proj_moneymanager.Object.Category;

import java.util.Date;
import java.util.UUID;

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
                    BillEntry.COLUMN_ID + " VARCHAR(36) PRIMARY KEY," +
                    BillEntry.COLUMN_USER_ID + " INTEGER," +
                    BillEntry.COLUMN_CATEGORY_ID + " VARCHAR(36)," +
                    BillEntry.COLUMN_NOTE + " VARCHAR(150)," +
                    BillEntry.COLUMN_TIMECREATE + " DATETIME," +
                    BillEntry.COLUMN_EXPENSE + " DOUBLE," +
                    BillEntry.COLUMN_SYNC_STATUS + " INTEGER);";
    private static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS " + DbContract.CategoryEntry.TABLE_NAME + " (" +
                    DbContract.CategoryEntry.COLUMN_ID + " VARCHAR(36) PRIMARY KEY," +
                    DbContract.CategoryEntry.COLUMN_USER_ID + " INTEGER," +
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
            db.execSQL(DROP_TABLE_BILL);
            db.execSQL(DROP_TABLE_USERINFORMATION);
            db.execSQL(DROP_TABLE_CATEGORY);
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
    public Cursor getUserInformation(long userID, SQLiteDatabase database) {
        String query = "SELECT *" +" FROM " + UserInformationEntry.TABLE_NAME + " WHERE " + UserInformationEntry._ID +"="+ userID;
        return database.rawQuery(query, null);
    }
    // Hàm xóa tất cả các bảng trong cơ sở dữ liệu
    public void clearAllTables(SQLiteDatabase db) {
        try {
            db.execSQL(DROP_TABLE_USERINFORMATION);
            db.execSQL(DROP_TABLE_BILL);
            db.execSQL(DROP_TABLE_CATEGORY);
            // Tạo lại các bảng sau khi xóa
            onCreate(db);
        } catch (SQLException e) {
            Log.e("DbHelper", "Error clearing all tables: " + e.getMessage());
        }
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
    //TABLE USER INFORMATION
    // Hàm truy vấn người dùng từ cơ sở dữ liệu local
    public Cursor getUserFromLocalDatabase(String userName, String password, SQLiteDatabase database) {
        String[] columns = {UserInformationEntry._ID, UserInformationEntry.COLUMN_FULL_NAME, UserInformationEntry.COLUMN_EMAIL, UserInformationEntry.COLUMN_PHONE_NUMBER};
        String selection = UserInformationEntry.COLUMN_USERNAME + " = ? AND " + UserInformationEntry.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {userName, password};

        // Thực hiện truy vấn
        return database.query(UserInformationEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    public int getUserID() {
        SQLiteDatabase db = null;
        int userID = -1;

        try {
            // Mở cơ sở dữ liệu
            db = this.getReadableDatabase();

            // Nếu có ít nhất một dòng trong bảng
            if (getRowCount(db) > 0 && getRowCount(db) < 2) {
                // Thực hiện truy vấn để lấy userID của dòng đầu tiên
                String query = "SELECT " + UserInformationEntry._ID +
                        " FROM " + UserInformationEntry.TABLE_NAME +
                        " LIMIT 1"; // Giới hạn kết quả lấy ra chỉ 1 dòng
                Cursor cursor = db.rawQuery(query, null);
                int columnIndexUserID = cursor.getColumnIndex(UserInformationEntry._ID);
                // Di chuyển con trỏ đến dòng đầu tiên
                if (cursor.moveToFirst()) {
                    // Lấy giá trị userID từ cột _ID
                    userID = cursor.getInt(columnIndexUserID);
                }

                // Đóng con trỏ cursor
                cursor.close();
            }
            else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối đến cơ sở dữ liệu
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return userID;
    }

    private int getRowCount(SQLiteDatabase db) {
        int count = 0;

        try {
            // Thực hiện truy vấn để lấy số lượng dòng trong bảng
            String countQuery = "SELECT * FROM " + UserInformationEntry.TABLE_NAME;
            Cursor cursor = db.rawQuery(countQuery, null);

            // Đếm số lượng dòng
            count = cursor.getCount();

            // Đóng con trỏ cursor
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    // TABLE BILL
    public Cursor readBillFromLocalDatabase(SQLiteDatabase database) {
        String query = "SELECT " +
                BillEntry.TABLE_NAME + "." + BillEntry.COLUMN_ID + ", " +
                BillEntry.COLUMN_USER_ID + ", " +
                BillEntry.COLUMN_CATEGORY_ID + ", " +
                BillEntry.COLUMN_NOTE + ", " +
                BillEntry.COLUMN_TIMECREATE + ", " +
                BillEntry.COLUMN_EXPENSE + ", " +
                BillEntry.COLUMN_SYNC_STATUS +
                " FROM " + BillEntry.TABLE_NAME +
                " ORDER BY " + BillEntry.COLUMN_TIMECREATE + " ASC";

        return database.rawQuery(query, null);
    }

    public String insertBillToLocalDatabaseFromApp(long userID, String categoryId, String note, Date timecreate, double money, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();

        String billID = UUID.randomUUID().toString();
        values.put(BillEntry.COLUMN_ID, billID);

        values.put(BillEntry.COLUMN_USER_ID, userID);
        values.put(BillEntry.COLUMN_CATEGORY_ID, categoryId);
        values.put(BillEntry.COLUMN_NOTE, note);
        values.put(BillEntry.COLUMN_TIMECREATE, timecreate.getTime());
        values.put(BillEntry.COLUMN_EXPENSE, money);
        values.put(BillEntry.COLUMN_SYNC_STATUS, synstatus);

        database.insert(BillEntry.TABLE_NAME, null, values);
        return billID;
    }

    public void updateBillInLocalDatabase(String billId, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(BillEntry.COLUMN_ID, billId);
//        values.put(BillEntry.COLUMN_USER_ID, userID);
//        values.put(BillEntry.COLUMN_CATEGORY_ID, categoryId);
//        values.put(BillEntry.COLUMN_NOTE, note);
//        values.put(BillEntry.COLUMN_TIMECREATE, datetime.getTime());
//        values.put(BillEntry.COLUMN_EXPENSE, money);
//
        values.put(BillEntry.COLUMN_SYNC_STATUS, synstatus);

        //mới sửa
        String selection = BillEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {billId};

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

    // TABLE CATEGORY
    public String insertCategoryToLocalDatabaseFromApp(long userID, String name, String icon, String color, int syncstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();

        String categoryID = UUID.randomUUID().toString();
        values.put(DbContract.CategoryEntry.COLUMN_ID, categoryID);

        values.put(DbContract.CategoryEntry.COLUMN_USER_ID, userID);
        values.put(DbContract.CategoryEntry.COLUMN_NAME, name);
        values.put(DbContract.CategoryEntry.COLUMN_ICON, icon);
        values.put(DbContract.CategoryEntry.COLUMN_COLOR, color);
        values.put(DbContract.CategoryEntry.COLUMN_SYNC_STATUS, syncstatus);

        database.insert(DbContract.CategoryEntry.TABLE_NAME, null, values);
        // Insert dữ liệu và trả về ID của hàng vừa thêm
        return categoryID;
    }
    public Cursor readCategoryFromLocalDatabase(SQLiteDatabase database) {
        String query = "SELECT " +
                DbContract.CategoryEntry.COLUMN_ID + ", " +
                DbContract.CategoryEntry.COLUMN_USER_ID + ", " +
                DbContract.CategoryEntry.COLUMN_NAME + ", " +
                DbContract.CategoryEntry.COLUMN_ICON + ", " +
                DbContract.CategoryEntry.COLUMN_COLOR + ", " +
                DbContract.CategoryEntry.COLUMN_SYNC_STATUS +
                " FROM " + DbContract.CategoryEntry.TABLE_NAME;

        return database.rawQuery(query, null);
    }
    public void updateSyncCategoryInLocalDatabase(String categoryId, int synstatus, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DbContract.CategoryEntry.COLUMN_ID, categoryId);
        values.put(DbContract.CategoryEntry.COLUMN_SYNC_STATUS, synstatus);

        String selection = DbContract.CategoryEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {categoryId};

        database.update(DbContract.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public boolean isCategoryNameExists(String name, long userID) {
        SQLiteDatabase database = getReadableDatabase();

        // Truy vấn kiểm tra xem đã tồn tại name trong cơ sở dữ liệu hay chưa
        String query = "SELECT * FROM " + DbContract.CategoryEntry.TABLE_NAME +
                " WHERE " + DbContract.CategoryEntry.COLUMN_NAME + " = ?" +
                " AND " + DbContract.CategoryEntry.COLUMN_USER_ID + " = " + userID;

        Cursor cursor = database.rawQuery(query, new String[]{name});

        boolean exists = cursor.getCount() > 0;

        // Đóng cursor sau khi kiểm tra xong
        cursor.close();

        return exists;
    }
    public boolean isCategoryNameExists(String categoryID, String name, long userID) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + DbContract.CategoryEntry.TABLE_NAME +
                " WHERE " + DbContract.CategoryEntry.COLUMN_NAME + " = ?" +
                " AND " + DbContract.CategoryEntry.COLUMN_USER_ID + " = ?" +
                " AND " + DbContract.CategoryEntry.COLUMN_ID + " <> ?";

        String[] selectionArgs = {name, String.valueOf(userID), categoryID};

        Cursor cursor = database.rawQuery(query, selectionArgs);

        boolean exists = cursor.getCount() > 0;

        cursor.close();

        return exists;
    }

    public String getCategoryNameById(String categoryId, SQLiteDatabase database) {
        String[] projection = {
                DbContract.CategoryEntry.COLUMN_NAME
        };

        String selection = DbContract.CategoryEntry.COLUMN_ID + " = ?";
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
        int columnIndexName = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_NAME);
        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(columnIndexName);
        }

        cursor.close();
        return categoryName;
    }
    public Category getItemCategory(String categoryID, SQLiteDatabase database) {
        Category category = null;

        String[] projection = {
                DbContract.CategoryEntry.COLUMN_ID,
                DbContract.CategoryEntry.COLUMN_USER_ID,
                DbContract.CategoryEntry.COLUMN_NAME,
                DbContract.CategoryEntry.COLUMN_ICON,
                DbContract.CategoryEntry.COLUMN_COLOR,
                DbContract.CategoryEntry.COLUMN_SYNC_STATUS
        };

        String selection = DbContract.CategoryEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {categoryID};

        Cursor cursor = database.query(
                DbContract.CategoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int columnIndexCategoryID = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_ID);
            int columnIndexUserID = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_USER_ID);
            int columnIndexName = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_NAME);
            int columnIndexIcon = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_ICON);
            int columnIndexColor = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_COLOR);
            int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_SYNC_STATUS);

            if (columnIndexCategoryID != -1 && columnIndexName != -1 &&
                    columnIndexColor != -1 && columnIndexIcon != -1 &&
                    columnIndexSyncStatus != -1) {

                String categoryId = cursor.getString(columnIndexCategoryID);
                long userid = cursor.getLong(columnIndexUserID);
                String name = cursor.getString(columnIndexName);
                String icon = cursor.getString(columnIndexIcon);
                String color = cursor.getString(columnIndexColor);
                int syncStatus = cursor.getInt(columnIndexSyncStatus);

                category = new Category(categoryId, userid, name, icon, color, syncStatus);
            }
        }
        cursor.close();
        return category;
    }
    public void updateCategoryById(Category categoryItem, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DbContract.CategoryEntry.COLUMN_NAME, categoryItem.getName());
        values.put(DbContract.CategoryEntry.COLUMN_ICON, categoryItem.getIcon());
        values.put(DbContract.CategoryEntry.COLUMN_COLOR, categoryItem.getColor());
        values.put(DbContract.CategoryEntry.COLUMN_SYNC_STATUS, categoryItem.getSyncStatus());

        String whereClause = DbContract.CategoryEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {categoryItem.getID()};

        try {
            database.update(DbContract.CategoryEntry.TABLE_NAME, values, whereClause, whereArgs);
        } catch (SQLException e) {
            Log.e("DbHelper", "Error updating category: " + e.getMessage());
        }
    }
    public void deleteCategoryById(String categoryId, SQLiteDatabase database) {
        try {
            // Delete the category from the local database
            String selection = DbContract.CategoryEntry.COLUMN_ID + " = ?";
            String[] selectionArgs = {categoryId};
            database.delete(DbContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);

            // You can add additional logic if needed

        } catch (SQLException e) {
            Log.e("DbHelper", "Error deleting category: " + e.getMessage());
        }
    }

}
