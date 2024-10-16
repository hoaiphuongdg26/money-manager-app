package com.example.proj_moneymanager.activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.example.proj_moneymanager.MD5Hasher;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.NetworkMonitor;
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

public class SignUp extends AppCompatActivity {
    EditText editTextYourName, editTextUserName, editTextPassword;
    ImageButton btnSignUp;
    TextView textViewMoveToLogin;
    //for google login
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    ImageButton btnGoogleSignUp;
    String yourName;
    String userName;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

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

        editTextYourName = findViewById(R.id.edittext_yourname);
        editTextUserName = findViewById(R.id.edittext_username);
        editTextPassword = findViewById(R.id.edittext_password);
        btnSignUp = findViewById(R.id.button_signup);
        //btnGoogleSignUp = findViewById(R.id.button_google);
        textViewMoveToLogin = findViewById(R.id.textview_moveToLogin);
        btnGoogleSignUp = findViewById(R.id.button_google);
        btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGoogleSignup();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourName = editTextYourName.getText().toString();
                userName = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();
                if(yourName.isEmpty()||userName.isEmpty()||password.isEmpty())
                    Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_all_fields), Toast.LENGTH_SHORT).show();
                else{
                    if(checkNetworkConnection()){
                        password = MD5Hasher.hashString(password);
                        performSignUp();
                    }
                    else Toast.makeText(SignUp.this, getString(R.string.No_network_connection), Toast.LENGTH_SHORT).show();
                }
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
                                        yourName = credential.getDisplayName();
                                        userName = credential.getId();
                                        password = MD5Hasher.hashString(credential.getId());
                                        performSignUp();
//                                        Toast.makeText(getApplicationContext(),"Welcome, "+ email,Toast.LENGTH_SHORT).show();
//
//                                        appConfig.saveLoginUsingGmail(true);
//                                        appConfig.updateUserLoginStatus(true);
//                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                    }
                                }
                                catch (ApiException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
    private void performGoogleSignup(){
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
    private void performSignUp() {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class)
                .performUserSignUp(userName, password, yourName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("ok")) {
                        if (response.body().getResultCode() == 1) {
                            Toast.makeText(getApplicationContext(), getString(R.string.Sign_up_successfully), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.User_already_exist), Toast.LENGTH_SHORT).show();
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
                Log.e("Retrofit", "Error: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getApplicationContext());
    }
}
