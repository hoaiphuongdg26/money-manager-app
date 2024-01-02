package com.example.proj_moneymanager.activities.Expense;

public class Category_Option {
    String nameCategory, iconCategory, colorCategory;
    public Category_Option(String nameCategory, String iconCategory, String colorCategory){
        this.nameCategory = nameCategory;
        this.iconCategory = iconCategory;
        this.colorCategory = colorCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public String getIconCategory() {
        return iconCategory;
    }

    public String getColorCategory() {
        return colorCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public void setIconCategory(String iconCategory) {
        this.iconCategory = iconCategory;
    }

    public void setColorCategory(String colorCategory) {
        this.colorCategory = colorCategory;
    }
}
