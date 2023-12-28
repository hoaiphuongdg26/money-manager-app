package com.example.proj_moneymanager.Object;

public class Category {
    int CategoryID;
    String Name;
    int Icon;
    String Color;

    public Category(int categoryID, String name) {
        CategoryID = categoryID;
        Name = name;
    }

    public int getCategoryID() {
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
}
