package com.example.proj_moneymanager.database;

public class DbContract {
    public static final int SYNC_STATUS_OK = 0;
    public static final int SYNC_STATUS_FAILED = 1;
    public static final String SERVER_URL = "http://192.168.3.185/money_management/";
    public static final String UI_UPDATE_BROADCAST = "com.example.proj_moneynanager.uiupdatebroadcast";
    public static final String DATABASE_NAME = "moneyManagement";

    // TABLE BILL
    public static final String TABLE_BILL = "BILL";
    public static final String TABLE_BILL_ID = "ID";
    public static final String TABLE_BILL_USERID = "UserID";
    public static final String TABLE_BILL_CATEGORYID = "CategoryID";
    public static final String TABLE_BILL_NOTE = "Note";
    public static final String TABLE_BILL_DATETIME = "Datetime";
    public static final String TABLE_BILL_MONEY = "Money";

    // TABLE CATEGORY

    public static final String TABLE_CATEGORY = "CATEGORY";
    public static final String TABLE_CATEGORY_ID = "ID";
    public static final String TABLE_CATEGORY_NAME = "Name";

    // TABLE PLAN
    public static final String TABLE_PLAN = "PLAN";
    public static final String TABLE_PLAN_ID = "ID";
    public static final String TABLE_PLAN_TARGET = "Target";
    public static final String TABLE_PLAN_MATURITY = "Maturity";
    public static final String TABLE_PLAN_PLANNAME = "PlanName";
    public static final String TABLE_PLAN_KINDOFTARGET = "KindOfTarget";
    public static final String TABLE_PLAN_METHOD = "Method";
    public static final String TABLE_PLAN_START = "Start";
    public static final String TABLE_PLAN_FINISH = "Finish";

    // TABLE INFORMATION
    public static final String TABLE_USERINFORMATION = "USERINFOMATION";
    public static final String TABLE_USERINFORMATION_ID = "ID";
    public static final String TABLE_USERINFORMATION_FULLNAME = "Fullname";
    public static final String TABLE_USERINFORMATION_USERNAME = "Username";
    public static final String TABLE_USERINFORMATION_PASSWORD = "Password";
    public static final String TABLE_USERINFORMATION_EMAIL = "Email";
    public static final String TABLE_USERINFORMATION_PHONENUMBER = "PhoneNumber";


    public static final String SYNC_STATUS = "syncstatus";
}
