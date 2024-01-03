package com.example.proj_moneymanager.Object;

import java.util.Date;

public class Bill {
    long BillID, UserID, CategoryID;
    String Note;
    Date Datetime;

    double Money;
    int SyncStatus;

    public Bill(long billID, long userID, long categoryID, String note, Date datetime, double money, int syncstatus) {
        BillID = billID;
        UserID = userID;
        CategoryID = categoryID;
        Note = note;
        Datetime = datetime;
        Money = money;
        SyncStatus = syncstatus;
    }

    public long getBillID() {
        return BillID;
    }

    public long getUserID() {
        return UserID;
    }

    public long getCategoryID() {
        return CategoryID;
    }

    public double getMoney() {
        return Money;
    }

    public Date getDatetime() {
        return Datetime;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public String getNote() {
        return Note;
    }
}
