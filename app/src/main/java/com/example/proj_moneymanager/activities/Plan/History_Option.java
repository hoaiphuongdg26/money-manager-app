package com.example.proj_moneymanager.activities.Plan;

public class History_Option {
    public String label, labelInfo, price;
    public int imageOption;

    public History_Option(String label, String labelInfo, int imageOption, String price) {
        this.label = label;
        this.labelInfo = labelInfo;
        this.imageOption = imageOption;
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelInfo() {
        return labelInfo;
    }

    public int getImageOption() {
        return imageOption;
    }
    public String getPrice(){
        return price;
    }
}