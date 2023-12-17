package com.example.proj_moneymanager.activities.Profile;

public class Profile_Option {
    public String label, labelInfo;
    public int imageOption;

    public Profile_Option(String label, String labelInfo, int imageOption) {
        this.label = label;
        this.labelInfo = labelInfo;
        this.imageOption = imageOption;
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
}