package com.example.proj_moneymanager.database;

import android.provider.BaseColumns;

public class DbContract {
    public static final int SYNC_STATUS_OK = 0;
    public static final int SYNC_STATUS_FAILED = 1;

    public static final String SERVER_URL = "http://172.16.1.186/money_management/sync.php/";
    public static final String UI_UPDATE_BROADCAST = "com.example.proj_moneynanager.uiupdatebroadcast";
    public static final String DATABASE_NAME = "moneyManagement";


    public static class BillEntry implements BaseColumns {
        public static final String TABLE_NAME = "BILL";
        public static final String COLUMN_USER_ID = "UserID";
        public static final String COLUMN_CATEGORY_ID = "CategoryID";
        public static final String COLUMN_NOTE = "Note";
        public static final String COLUMN_TIMECREATE = "TimeCreate";
        public static final String COLUMN_EXPENSE = "Expense";
        public static final String COLUMN_SYNC_STATUS = "sync_status";
    }

    // Bảng CATEGORY
    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "CATEGORY";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_COLOR = "Color";
        public static final String COLUMN_ICON = "Icon";
        public static final String COLUMN_SYNC_STATUS = "sync_status";

    }

    // Bảng PLAN
    public static class PlanEntry implements BaseColumns {
        public static final String TABLE_NAME = "PLAN";
        public static final String COLUMN_TARGET = "Target";
        public static final String COLUMN_MATURITY = "Maturity";
        public static final String COLUMN_PLAN_NAME = "PlanName";
        public static final String COLUMN_KIND_OF_TARGET = "KindOfTarget";
        public static final String COLUMN_METHOD = "Method";
        public static final String COLUMN_START = "Start";
        public static final String COLUMN_FINISH = "Finish";
        public static final String COLUMN_SYNC_STATUS = "sync_status";
    }

    // Bảng USER_INFORMATION
    public static class UserInformationEntry implements BaseColumns {
        public static final String TABLE_NAME = "USER_INFORMATION";
        public static final String COLUMN_FULL_NAME = "Fullname";
        public static final String COLUMN_USERNAME = "Username";
        public static final String COLUMN_PASSWORD = "Password";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_PHONE_NUMBER = "PhoneNumber";
        public static final String COLUMN_SYNC_STATUS = "sync_status";
    }
}
