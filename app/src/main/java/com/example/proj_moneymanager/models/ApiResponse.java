package com.example.proj_moneymanager.models;
import com.google.gson.annotations.SerializedName;
public class ApiResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("result_code")
    private int ResultCode;
    @SerializedName("name")
    private String name;
    public String getStatus(){
        return status;
    }
    public int getResultCode(){
        return ResultCode;
    }
    public String getName(){
        return name;
    }
}
