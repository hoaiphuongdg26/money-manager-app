package com.example.proj_moneymanager.Object;

public class Category {
    long UserID;
    String CategoryID;
    String Name;
    String Icon;
    String Color;
    int SyncStatus;

    public Category(long userID) {
        UserID = userID;
    }

    public Category(String categoryID, long userID, String name, String icon, String color, int syncStatus) {
        this.CategoryID = categoryID;
        this.UserID = userID;
        this.Name = name;
        this.Icon = icon;
        this.Color = color;
        this.SyncStatus = syncStatus;
    }

    public String getID() {
        return CategoryID;
    }

    public String getName() {
        return Name;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setColor(String color) {
        Color = color;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getColor() {
        return Color;
    }

    public String getIcon() {
        return Icon;
    }

}
