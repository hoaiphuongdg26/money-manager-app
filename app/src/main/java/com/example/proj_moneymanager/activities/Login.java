package com.example.proj_moneymanager.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.models.ApiResponse;
import com.example.proj_moneymanager.retrofit.ApiClient;
import com.example.proj_moneymanager.retrofit.ApiInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    ImageButton btnLogin;
    EditText editTextUserName, editTextPassword;
    TextView textViewSignUp;
    CheckBox checkBoxIsRememberLogin;
    private boolean isRememberLogin = false;
    private AppConfig appConfig;
    String UserName, Password;
    //for google login
    private FirebaseAuth mAuth;
    ImageButton bt_googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        isRememberLogin = appConfig.isRememberLoginChecked();
        if(appConfig.isUserLogin() && isRememberLogin){
            UserName = appConfig.getUserName();
            Password = appConfig.getUserPassword();
            if(appConfig.isLoginUsingGmail())
            {
                //Google Login
                performGoogleLogin();
            } else {
                performLogin();
            }
        }
        else{
            setContentView(R.layout.login);

            editTextUserName = (EditText) findViewById(R.id.edittext_username);
            editTextPassword = (EditText) findViewById(R.id.edittext_password);

            textViewSignUp = (TextView)findViewById(R.id.textview_moveToSignup);
            //Set check box remeberlogin
            checkBoxIsRememberLogin = (CheckBox)findViewById(R.id.checkbox_rememberLogin);
            checkBoxIsRememberLogin.setChecked(appConfig.isRememberLoginChecked());

            btnLogin = (ImageButton) findViewById(R.id.button_login);
            bt_googleSignIn = (ImageButton) findViewById(R.id.button_google);
            //Google Button Click -> Login with Google
            bt_googleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserName = editTextUserName.getText().toString();
                    Password = editTextPassword.getText().toString();
                    if(!UserName.equals("")&&!Password.equals("")){
                        //Start ProgressBar first (set visibility VISIBLE)
                        performGoogleLogin();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please enter Login information", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserName = editTextUserName.getText().toString();
                    Password = editTextPassword.getText().toString();
                    if(!UserName.equals("")&&!Password.equals("")){
                        //Start ProgressBar first (set visibility VISIBLE)
                        performLogin();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please enter Login information", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            checkBoxIsRememberLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRememberLogin = checkBoxIsRememberLogin.isChecked();
                    appConfig.saveIsRememberLoginClicked(isRememberLogin);
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
    }
    private void performGoogleLogin(){
        mAuth.signInWithEmailAndPassword(UserName, Password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String name = user.getDisplayName();
                            Toast.makeText(getApplicationContext(),"Welcome, "+ name,Toast.LENGTH_SHORT).show();
                            appConfig.saveLoginUsingGmail(true);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void performLogin(){
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).performUserLogIn(UserName, Password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("ok")) {
                        if (response.body().getResultCode() == 1) {
                            String name = response.body().getName();

                            //Lưu name password
                            if(isRememberLogin){
                                appConfig.saveLoginUsingGmail(false);
                                appConfig.updateUserLoginStatus(true);
                                appConfig.saveUserName(UserName);
                                appConfig.saveUserPassword(Password);
                                appConfig.saveIsRememberLoginClicked(true);
                            }
                            Toast.makeText(getApplicationContext(), "Welcome, "+ name, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
