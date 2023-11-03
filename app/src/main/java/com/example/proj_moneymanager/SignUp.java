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
import com.vishnusivadas.advanced_httpurlconnection.PutData;

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
                String fullname = editTextYourName.getText().toString();
                String UserName = editTextUserName.getText().toString();
                String Password = editTextPassword.getText().toString();

                if (!fullname.equals("") && !UserName.equals("") && !Password.equals("")) {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Parameters to be sent
                            String[] field = new String[3];
                            field[0] = "fullname";
                            field[1] = "UserName";
                            field[2] = "Password";

                            // Data to be sent
                            String[] data = new String[3];
                            data[0] = fullname;
                            data[1] = UserName;
                            data[2] = Password;

                            PutData putData = new PutData("http://localhost/money_management/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Sign up Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
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
}
