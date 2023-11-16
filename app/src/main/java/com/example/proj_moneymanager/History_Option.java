package com.example.proj_moneymanager;

public class History_Option {
    public String label, labelInfo;
    public int imageOption;

    public History_Option(String label, String labelInfo, int imageOption) {
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
