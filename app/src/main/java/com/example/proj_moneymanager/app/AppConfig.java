package com.example.proj_moneymanager.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.proj_moneymanager.R;

public class AppConfig {
    private Context context;
    private SharedPreferences sharedPreferences;
    public AppConfig(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_key),Context.MODE_PRIVATE);
    }
    public boolean isUserLogin(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_user_login),false);
    }
    public void updateUserLoginStatus(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_is_user_login),status);
        editor.commit();
    }
    public void saveUserName(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_name_of_user),name);
        editor.commit();
    }
    public String getUserName(){
        return sharedPreferences.getString(context.getString(R.string.pref_name_of_user),"");
    }
    public void saveUserPassword(String passwd){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_user_password),passwd);
        editor.commit();
    }
    public String getUserPassword(){
        return sharedPreferences.getString(context.getString(R.string.pref_user_password),"");
    }
    public void saveIsRememberLoginClicked(boolean isChecked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_is_remember_login_clicked),isChecked);
        editor.commit();
    }
    public boolean isRememberLoginChecked(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_remember_login_clicked),false);
    }
}
