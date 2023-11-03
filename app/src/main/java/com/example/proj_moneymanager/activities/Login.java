package com.example.proj_moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.models.ApiResponse;
import com.example.proj_moneymanager.retrofit.ApiClient;
import com.example.proj_moneymanager.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    performLogin(UserName,Password);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter Login information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void performLogin(String UserName, String Password){
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).performUserLogIn(UserName, Password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("ok")) {
                        if (response.body().getResultCode() == 1) {
                            String name = response.body().getName();
                            Toast.makeText(getApplicationContext(), "Login successfully, Fullname = "+ name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Can't connect to database", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
