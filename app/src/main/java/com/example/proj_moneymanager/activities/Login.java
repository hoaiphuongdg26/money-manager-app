package com.example.proj_moneymanager.activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.proj_moneymanager.AsyncTasks.GetServerData;
import com.example.proj_moneymanager.MD5Hasher;
import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.UserInformation;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Instruction.Instruction;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
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
    public static DbHelper database;
    UserInformation userInformation;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kiểm tra trạng thái đã xem instruction hay chưa
        SharedPreferences preferences = getSharedPreferences("MyPreferencesInstruction", MODE_PRIVATE);
        boolean hasViewedInstruction = preferences.getBoolean("hasViewedInstruction", false);

        if (!hasViewedInstruction) {
            // Nếu chưa xem instruction, mở nó và cập nhật trạng thái
            Intent intent = new Intent(Login.this, Instruction.class);
            startActivity(intent);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("hasViewedInstruction", true);
            editor.apply();

            finish();
        } else {
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
                    .setAutoSelectEnabled(false)
                    .build();

            isRememberLogin = appConfig.isRememberLoginChecked();
            if (appConfig.isUserLogin()) {
                if (checkNetworkConnection()) {
                    if (appConfig.isLoginUsingGmail()) {
                        //Google Login
                        performGoogleLogin();
                    } else {
                        UserName = appConfig.getUserName();
                        Password = appConfig.getUserPassword();
                        performLogin();
                    }
                } else {
                    UserName = appConfig.getUserName();
                    Password = appConfig.getUserPassword();
                    performLoginOffline();
                }
            } else {
                setContentView(R.layout.login);

                editTextUserName = (EditText) findViewById(R.id.edittext_username);
                editTextPassword = (EditText) findViewById(R.id.edittext_password);

                textViewSignUp = (TextView) findViewById(R.id.textview_moveToSignup);
                //Set check box remember login
                checkBoxIsRememberLogin = (CheckBox) findViewById(R.id.checkbox_rememberLogin);
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
                        if (checkNetworkConnection()) {
                            UserName = editTextUserName.getText().toString();
                            Password = MD5Hasher.hashString(editTextPassword.getText().toString());
                            if (!UserName.equals("") && !editTextPassword.getText().toString().equals("")) {
                                //Start ProgressBar first (set visibility VISIBLE)
                                performLogin();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.Please_enter_Login_information), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.No_network_connection), Toast.LENGTH_SHORT).show();
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
    }
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                try{
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();
                    if(idToken!=null){
                        UserName = credential.getId();
                        //khong cho lay password gmail
                        Password = MD5Hasher.hashString(credential.getId());//credential.getPassword();
                        performLogin();
//                        String email = credential.getId();
//                        Toast.makeText(getApplicationContext(),"Welcome, "+ email,Toast.LENGTH_SHORT).show();
//
                        appConfig.saveLoginUsingGmail(true);
                        //appConfig.updateUserLoginStatus(true);
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
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
                        Toast.makeText(getApplicationContext(), getString(R.string.Fail_to_login_with_Google), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void performLogin(){
            Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).performUserLogIn(UserName,Password);
            call.enqueue(new Callback<ApiResponse>() {

                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.code() == 200) {
                        ApiResponse apiResponse = response.body();
                        if (response.body().getStatus().equals("ok")) {
                            if (response.body().getResultCode() == 1) {
                                ApiResponse.UserData userData = apiResponse.getUserData();
                                long UserID = userData.getUserID();
                                String uFullName = userData.getFullName();
                                String uUserName = userData.getUserName();
                                String uPassword = userData.getPassword();
                                String uEmail = userData.getEmail();
                                String uPhoneNumber = userData.getPhoneNumber();
                                //Lưu name password
                                if(isRememberLogin){
                                    appConfig.saveLoginUsingGmail(false);
                                    appConfig.updateUserLoginStatus(true);
                                    appConfig.saveUserName(UserName);
                                    appConfig.saveUserPassword(uPassword);
                                    appConfig.saveIsRememberLoginClicked(true);
                                }
//                                userInformation = new UserInformation(userID, FullName,UserName, Password,Email, PhoneNumber);
                                //Lưu info user vào database local
                                DbHelper dbHelper = new DbHelper(getApplicationContext());
                                SQLiteDatabase database = dbHelper.getWritableDatabase();
                                dbHelper.onCreate(database);
                                dbHelper.insertUserToLocalDatabase(String.valueOf(UserID), uFullName,uUserName, uPassword,uEmail, uPhoneNumber,1, database);
                                //Đưa dữ liệu mà remote không có lên POST

                                //asynctask kéo dữ liệu từ remote về GET
                                GetServerData getServerData = new GetServerData(Login.this, UserID);
                                getServerData.execute(UserID);
                                //add class vo day

                                dbHelper.close();
                                appConfig.loadLocale();
                                //CreateSqliteDb();
                                //Switch to Home
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("UserID", UserID);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.Wrong_username_or_password), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.Wrong_username_or_password), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Can't connect to database", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.e("API Call Failure", "Error: " + t.getMessage()); // Log lỗi
                    Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            });
    }
    private void performLoginOffline() {
        // Lấy thông tin đăng nhập từ cơ sở dữ liệu local
        DbHelper dbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getUserFromLocalDatabase(UserName, Password, database);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int columnIndexUserID = cursor.getColumnIndex(DbContract.UserInformationEntry._ID);
                long userID = cursor.getLong(columnIndexUserID);

                // Đóng cursor và database khi đã sử dụng xong
                cursor.close();
                dbHelper.close();

                // Chuyển đến màn hình chính và truyền UserID qua Intent
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("UserID", userID);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Không có thông tin đăng nhập offline khớp với cơ sở dữ liệu local
            Toast.makeText(getApplicationContext(), getString(R.string.No_offline_login_info), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getApplicationContext());
    }
}
