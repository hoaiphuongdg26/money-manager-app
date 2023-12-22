package com.example.proj_moneymanager.Object;

import java.io.Serializable;

public class UserInformation implements Serializable {
    int UserID;
    String FullName, UserName, PassWord, Email, PhoneNumber;

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

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
