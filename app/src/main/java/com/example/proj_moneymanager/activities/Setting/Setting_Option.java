package com.example.proj_moneymanager.activities.Setting;

public class Setting_Option {
    public String label;
    public int imageOption;

    public Setting_Option(String label, int imageOption) {
        this.label = label;
        this.imageOption = imageOption;
    }

    public String getLabel() {
        return label;
    }
    public int getImageOption() {
        return imageOption;
    }
}
