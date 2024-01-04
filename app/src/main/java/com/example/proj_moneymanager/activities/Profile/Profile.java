package com.example.proj_moneymanager.activities.Profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.app.AppConfig;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    Button buttonLogout;
    private AppConfig appConfig;

    ListView lv_profileOption;
    ArrayList<Profile_Option> arr_profileOption;
    SignInClient oneTapClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(this);
        oneTapClient = Identity.getSignInClient(this);

        setContentView(R.layout.fragment_profile);

        //Xử lý Profile Adapter cho listview
        lv_profileOption = (ListView) findViewById(R.id.lv_optProfile);
        arr_profileOption = new ArrayList<Profile_Option>();

        arr_profileOption.add(new Profile_Option(getString(R.string.Name), "Group03", R.drawable.icon_person_profile));
        arr_profileOption.add(new Profile_Option(getString(R.string.Password), "********", R.drawable.icon_lock));
        arr_profileOption.add(new Profile_Option(getString(R.string.Switch_Account), "group03@gmail.com", R.drawable.icon_switch));
        arr_profileOption.add(new Profile_Option(getString(R.string.Notification), "", R.drawable.icon_notification_fill));

        ProfileAdapter profileAdapter = new ProfileAdapter(
                Profile.this,
                arr_profileOption
        );
        lv_profileOption.setAdapter(profileAdapter);

        //Xử lí Logout
//        buttonLogout = (Button) findViewById(R.id.button_logout);
//        buttonLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                appConfig.updateUserLoginStatus(false);
//                appConfig.saveIsRememberLoginClicked(false);
//                if(appConfig.isLoginUsingGmail()){
//                    oneTapClient.signOut();
//                    appConfig.saveLoginUsingGmail(false);
//                }
//                //Move to login activity
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
}