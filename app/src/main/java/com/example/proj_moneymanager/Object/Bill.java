package com.example.proj_moneymanager.Object;

import java.util.Date;

public class Bill {
    int BillID, UserID, CategoryID;
    String Note;
    Date Datetime;
    //Date Date; chua test dc
    double Money;
    int SyncStatus;

    public Bill(int billID, int userID, int categoryID, String note, Date datetime, double money, int syncstatus) {
        BillID = billID;
        UserID = userID;
        CategoryID = categoryID;
        Note = note;
        Datetime = datetime;
        Money = money;
        SyncStatus = syncstatus;
    }

    public int getBillID() {
        return BillID;
    }

    public int getUserID() {
        return UserID;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public double getMoney() {
        return Money;
    }
}
