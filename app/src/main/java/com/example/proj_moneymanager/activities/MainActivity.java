package com.example.proj_moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Profile.Profile;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButtonPersonal;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //Bỏ title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide(); //ẩn action bar

            setContentView(R.layout.home);
            //Xử lí khi click button Personal
            imageButtonPersonal = (ImageButton) findViewById(R.id.button_iconPersonal);
            imageButtonPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Chuyển qua activity profile
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
}