package com.example.proj_moneymanager.Object;

import static com.example.proj_moneymanager.database.DbContract.UserInformationEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;

import java.io.Serializable;

public class UserInformation implements Serializable {
    long UserID;
    String FullName, UserName, PassWord, Email, PhoneNumber;

    public UserInformation() {
        // Constructor mặc định
    }

    public UserInformation(long userID, String fullName, String userName, String passWord, String email, String phoneNumber) {
        UserID = userID;
        FullName = fullName;
        UserName = userName;
        PassWord = passWord;
        Email = email;
        PhoneNumber = phoneNumber;
    }

    public String getFullName() {
        return FullName;
    }
    public String getPassWord() {
        return PassWord;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    // Các phương thức lấy thông tin từ cơ sở dữ liệu
    public static String getFullName(Context context, long userID) {
        String fullName = "";

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {UserInformationEntry.COLUMN_FULL_NAME};
        String selection = UserInformationEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = database.query(
                UserInformationEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        int columnIndexUserFullname = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_FULL_NAME);
        if (cursor != null && cursor.moveToFirst()) {
            fullName = cursor.getString(columnIndexUserFullname);
            cursor.close();
        }

        return fullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getUserName(Context context, int userID) {
        String userName = "";

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {UserInformationEntry.COLUMN_USERNAME};
        String selection = UserInformationEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = database.query(
                UserInformationEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        int columnIndexUserFullname = cursor.getColumnIndex(UserInformationEntry.COLUMN_USERNAME);
        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(columnIndexUserFullname);
            cursor.close();
        }

        return userName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    // Các phương thức khác tương tự cho các trường dữ liệu khác

    //...

}
