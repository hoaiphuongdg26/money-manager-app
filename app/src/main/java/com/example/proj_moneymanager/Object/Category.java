package com.example.proj_moneymanager.Object;

public class Category {
    int CategoryID;
    String Name;
    String Icon;
    String Color;
    int SyncStatus;

    public Category(int categoryID, String name, String icon, String color, int syncStatus) {
        this.CategoryID = categoryID;
        this.Name = name;
        this.Icon = icon;
        this.Color = color;
        this.SyncStatus = syncStatus;
    }

    public int getID() {
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
