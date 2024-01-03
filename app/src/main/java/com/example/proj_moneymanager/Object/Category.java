package com.example.proj_moneymanager.Object;

public class Category {
    long CategoryID;
    String Name;
    String Icon;
    String Color;
    int SyncStatus;

    public Category(long categoryID, String name, String icon, String color, int syncStatus) {
        this.CategoryID = categoryID;
        this.Name = name;
        this.Icon = icon;
        this.Color = color;
        this.SyncStatus = syncStatus;
    }

    public long getID() {
        return CategoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getColor() {
        return Color;
    }

    public String getIcon() {
        return Icon;
    }

}
