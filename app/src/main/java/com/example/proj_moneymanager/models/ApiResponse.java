package com.example.proj_moneymanager.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("result_code")
    private int resultCode;

    @SerializedName("userData")
    private UserData userData;

    public String getStatus() {
        return status;
    }

    public int getResultCode() {
        return resultCode;
    }

    public UserData getUserData() {
        return userData;
    }

    public class UserData {
        @SerializedName("userID")
        private int userID;

        @SerializedName("FullName")
        private String fullName;

        @SerializedName("UserName")
        private String userName;

        @SerializedName("Password")
        private String password;

        @SerializedName("Email")
        private String email;

        @SerializedName("PhoneNumber")
        private String phoneNumber;


        public int getUserID() {
            return userID;
        }

        public String getFullName() {
            return fullName;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
    public static ApiResponse decodeJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, ApiResponse.class);
    }
}
