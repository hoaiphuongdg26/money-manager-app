package com.example.proj_moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.proj_moneymanager.R;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButtonPersonal;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
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