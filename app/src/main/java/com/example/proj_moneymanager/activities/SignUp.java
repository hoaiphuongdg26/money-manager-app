package com.example.proj_moneymanager.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.models.ApiResponse;
import com.example.proj_moneymanager.retrofit.ApiClient;
import com.example.proj_moneymanager.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    EditText editTextYourName, editTextUserName, editTextPassword;
    Button btnSignUp;
    TextView textViewMoveToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        editTextYourName = findViewById(R.id.edittext_yourname);
        editTextUserName = findViewById(R.id.edittext_username);
        editTextPassword = findViewById(R.id.edittext_password);
        btnSignUp = findViewById(R.id.button_signup);
        textViewMoveToLogin = findViewById(R.id.textview_moveToSignup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignUp();
                //SET PROCESS BAR
            }
        });
        textViewMoveToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class); // Change to your login activity
                startActivity(intent);
                finish();
            }
        });
    }
    private void performSignUp() {
        String yourName = editTextYourName.getText().toString();
        String userName = editTextUserName.getText().toString();
        String password = editTextPassword.getText().toString();

        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class)
                .performUserSignUp(userName, password, yourName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("ok")) {
                        if (response.body().getResultCode() == 1) {
                            Toast.makeText(getApplicationContext(), "Sign up successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "User already exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Can't connect to database", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
