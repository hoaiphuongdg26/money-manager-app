package com.example.proj_moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.models.ApiResponse;
import com.example.proj_moneymanager.retrofit.ApiClient;
import com.example.proj_moneymanager.retrofit.ApiInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageButton bt_googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        if(appConfig.isUserLogin()){
            if(appConfig.isLoginUsingGmail())
            {
                //Google Login
                String name = GoogleSignIn.getLastSignedInAccount(this).getDisplayName();
                Toast.makeText(getApplicationContext(),"Wellcome, "+ name,Toast.LENGTH_SHORT).show();
                //Start Home activity
                appConfig.saveLoginUsingGmail(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                UserName = appConfig.getUserName();
                Password = appConfig.getUserPassword();
                isRememberLogin = appConfig.isRememberLoginChecked();
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
            //performGooglelogin()
            //Get Email -> connect to DB -> get FullName or other information
            bt_googleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performGoogleLogin();
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
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String name = account.getDisplayName();
                Toast.makeText(getApplicationContext(),"Wellcome, "+ name,Toast.LENGTH_SHORT).show();
                //Start Home activity
                appConfig.saveLoginUsingGmail(true);
                appConfig.updateUserLoginStatus(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Can't login with Google account",Toast.LENGTH_SHORT).show();
                //throw new RuntimeException(e);
            }
        }
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
                            Toast.makeText(getApplicationContext(), "Wellcome, "+ name, Toast.LENGTH_SHORT).show();
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
