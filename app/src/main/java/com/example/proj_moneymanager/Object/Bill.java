package com.example.proj_moneymanager.Object;

public class Bill {
    int BillID, UserID, CategoryID, Money;
    //Date Date; chua test dc

    public Bill(int billID, int userID, int categoryID, int money) {
        BillID = billID;
        UserID = userID;
        CategoryID = categoryID;
        Money = money;
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

    public int getMoney() {
        return Money;
    }
}
