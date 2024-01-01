package com.example.proj_moneymanager.activities.Plan;

import java.util.Date;

public class History_Option {
    public String label, labelInfo, price;
    public int imageOption, sync, userID;
    public Date DateTime;


    public History_Option(Date DateTime, int userID, String label, String labelInfo, int imageOption, String price, int sync) {
        this.DateTime = DateTime;
        this.userID = userID;
        this.label = label;
        this.labelInfo = labelInfo;
        this.imageOption = imageOption;
        this.price = price;
        this.sync = sync;
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
    public int getSync() { return sync;}
    public int getUserID() { return userID;}

    public Date getDateTime() { return DateTime;}
}