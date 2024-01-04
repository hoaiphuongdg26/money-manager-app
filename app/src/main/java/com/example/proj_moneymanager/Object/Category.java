package com.example.proj_moneymanager.Object;

public class Category {
    long UserID;
    String CategoryID;
    String Name;
    String Icon;
    String Color;
    int SyncStatus;

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
