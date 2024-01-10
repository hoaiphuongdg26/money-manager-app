package com.example.proj_moneymanager.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.example.proj_moneymanager.R;

import java.util.Locale;

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
    public boolean isLoginUsingGmail(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_login_using_gmail),false);
    }
    public void saveLoginUsingGmail(boolean isUsingGmail){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_is_login_using_gmail),isUsingGmail);
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
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        //save dataa vo shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("Language Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("Language", lang);
        edit.commit();
    }
    public void loadLocale() {
        SharedPreferences preferences = context.getSharedPreferences("Language Setting", Activity.MODE_PRIVATE);
        String language = preferences.getString("Language", "");
        setLocale(language);
    }
    public String getCurrentLanguage(){
        SharedPreferences preferences = context.getSharedPreferences("Language Setting", Activity.MODE_PRIVATE);
        return preferences.getString("Language","");
    }

}
