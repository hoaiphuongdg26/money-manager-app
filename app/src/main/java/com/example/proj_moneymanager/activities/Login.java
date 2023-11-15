package com.example.proj_moneymanager.activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.models.ApiResponse;
import com.example.proj_moneymanager.retrofit.ApiClient;
import com.example.proj_moneymanager.retrofit.ApiInterface;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    ImageButton bt_googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(this);
        // Initialize
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Not only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        isRememberLogin = appConfig.isRememberLoginChecked();
        if(appConfig.isUserLogin()){
            if(appConfig.isLoginUsingGmail())
            {
                //Google Login
                performGoogleLogin();
            } else {
                UserName = appConfig.getUserName();
                Password = appConfig.getUserPassword();
                performLogin();
            }
        }
        else{
            setContentView(R.layout.login);

            editTextUserName = (EditText) findViewById(R.id.edittext_username);
            editTextPassword = (EditText) findViewById(R.id.edittext_password);

            textViewSignUp = (TextView)findViewById(R.id.textview_moveToSignup);
            //Set check box remember login
            checkBoxIsRememberLogin = (CheckBox)findViewById(R.id.checkbox_rememberLogin);
            checkBoxIsRememberLogin.setChecked(appConfig.isRememberLoginChecked());

            btnLogin = (ImageButton) findViewById(R.id.button_login);
            bt_googleSignIn = (ImageButton) findViewById(R.id.button_google);
            //Google Button Click -> Login with Google
            bt_googleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //Start ProgressBar first (set visibility VISIBLE)
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
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                try{
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();
                    if(idToken!=null){
                        String email = credential.getId();
                        Toast.makeText(getApplicationContext(),"Welcome, "+ email,Toast.LENGTH_SHORT).show();

                        appConfig.saveLoginUsingGmail(true);
                        appConfig.updateUserLoginStatus(true);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (ApiException e){
                    e.printStackTrace();
                }
            }
        }
    });
    private void performGoogleLogin(){
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        IntentSenderRequest intentSenderRequest =
                        new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
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
