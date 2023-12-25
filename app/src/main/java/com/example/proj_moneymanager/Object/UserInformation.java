package com.example.proj_moneymanager.Object;

import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_EMAIL;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_FULLNAME;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_ID;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_PASSWORD;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_PHONENUMBER;
import static com.example.proj_moneymanager.database.DbContract.TABLE_USERINFORMATION_USERNAME;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proj_moneymanager.database.DbHelper;

import java.io.Serializable;

public class UserInformation implements Serializable {
    int UserID;
    String FullName, UserName, PassWord, Email, PhoneNumber;
    public UserInformation(){
        return;
    }

    public UserInformation(int userID, String fullName, String userName, String passWord, String email, String phoneNumber) {
        UserID = userID;
        FullName = fullName;
        UserName = userName;
        PassWord = passWord;
        Email = email;
        PhoneNumber = phoneNumber;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getFullName(Context context, int UserID) {

        String fullName = null; // Giá trị mặc định nếu không tìm thấy

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {TABLE_USERINFORMATION_FULLNAME};
        String selection = TABLE_USERINFORMATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(UserID)};

        Cursor cursor = database.query(
                TABLE_USERINFORMATION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndex(TABLE_USERINFORMATION_FULLNAME));
            cursor.close();
        }

        return fullName;
    }


    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getUserName(Context context, int userID) {

        String userName = ""; // Giá trị mặc định nếu không tìm thấy

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {TABLE_USERINFORMATION_USERNAME};
        String selection = TABLE_USERINFORMATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = database.query(
                TABLE_USERINFORMATION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndex(TABLE_USERINFORMATION_USERNAME));
            cursor.close();
        }

        return userName;
    }


    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword(Context context, int userID) {

        String password = ""; // Giá trị mặc định nếu không tìm thấy

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {TABLE_USERINFORMATION_PASSWORD};
        String selection = TABLE_USERINFORMATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = database.query(
                TABLE_USERINFORMATION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(TABLE_USERINFORMATION_PASSWORD));
            cursor.close();
        }

        return password;
    }


    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getEmail(Context context, int userID) {

        String email = ""; // Giá trị mặc định nếu không tìm thấy

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {TABLE_USERINFORMATION_EMAIL};
        String selection = TABLE_USERINFORMATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = database.query(
                TABLE_USERINFORMATION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(TABLE_USERINFORMATION_EMAIL));
            cursor.close();
        }

        return email;
    }


    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber(Context context, int userID) {

        String phoneNumber = ""; // Giá trị mặc định nếu không tìm thấy

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] columns = {TABLE_USERINFORMATION_PHONENUMBER};
        String selection = TABLE_USERINFORMATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = database.query(
                TABLE_USERINFORMATION,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            phoneNumber = cursor.getString(cursor.getColumnIndex(TABLE_USERINFORMATION_PHONENUMBER));
            cursor.close();
        }

        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
