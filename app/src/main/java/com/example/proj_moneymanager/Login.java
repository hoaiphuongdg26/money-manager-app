package com.example.proj_moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Login extends AppCompatActivity {
    Button btnLogin;
    EditText editTextUserName, editTextPassword;
    TextView textViewSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editTextUserName = (EditText) findViewById(R.id.edittext_username);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        textViewSignUp = (TextView)findViewById(R.id.textview_moveToSignup);
        btnLogin = (Button)findViewById(R.id.button_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserName, Password;
                UserName = editTextUserName.getText().toString();
                Password = editTextPassword.getText().toString();

                if(!UserName.equals("")&&!Password.equals("")){

                    //Start ProgressBar first (set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting read and write data with URL
                            //Creating array for parameters

                            String[] field = new String[2];
                            field[0] = "UserName";
                            field[1] = "Password";

                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = UserName;
                            data[1] = Password;
                            PutData putData = new PutData("http://172.16.1.114/money_management/login.php","POST",field,data);
                            if(putData.startPut()){
                                if(putData.onComplete()){
                                    String result = putData.getResult();
                                    if(result.equals("Sign up Success")){
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });
}
}
