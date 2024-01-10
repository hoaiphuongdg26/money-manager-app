package com.example.proj_moneymanager.activities.Profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.Object.UserInformation;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.DialogChangeNameBinding;
import com.example.proj_moneymanager.databinding.DialogChangePasswordBinding;
import com.example.proj_moneymanager.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private static Context context;
    UserInformation userInformation;
    private AppConfig appConfig;
//    private Identity.SignInClient oneTapClient;
    private ListView lv_profileOption;
    private ArrayList<Profile_Option> arr_profileOption;
    private Button buttonLogout;
    private SignInClient oneTapClient;
    private GoogleSignInClient googleSignInClient;
    public ProfileFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        appConfig = new AppConfig(getContext());
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        userInformation = new UserInformation();
        userInformation.setUserID(getArguments().getLong("UserID", 0));
        Cursor cursor = dbHelper.getUserInformation(userInformation.getUserID(),database);
        if(cursor.moveToFirst()){
            int columnIndexUserFullname = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_FULL_NAME);
            int columnIndexUserName = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_USERNAME);
            int columnIndexUserPassword = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_PASSWORD);
            int columnIndexEmail = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_EMAIL);

            String fullName = cursor.getString(columnIndexUserFullname);
            String userName = cursor.getString(columnIndexUserName);
            String password = cursor.getString(columnIndexUserPassword);
            String email = cursor.getString(columnIndexEmail);

            lv_profileOption = view.findViewById(R.id.lv_optProfile);
            arr_profileOption = new ArrayList<Profile_Option>();
            String hiddenPasswd = "";
            for(int i = 0; i<password.length();i++) hiddenPasswd+="*";
            // Thêm vào danh sách
            arr_profileOption.add(new Profile_Option(getString(R.string.Name), fullName, R.drawable.icon_person_profile));
            arr_profileOption.add(new Profile_Option(getString(R.string.Password), hiddenPasswd, R.drawable.icon_lock));
            arr_profileOption.add(new Profile_Option(getString(R.string.Email), email, R.drawable.icon_email_profile));
            arr_profileOption.add(new Profile_Option(getString(R.string.Notification), "", R.drawable.icon_notification_fill));
        }

        ProfileAdapter profileAdapter = new ProfileAdapter(
                requireActivity(),
                arr_profileOption
        );
        lv_profileOption.setAdapter(profileAdapter);

        oneTapClient = Identity.getSignInClient(context);
        buttonLogout = view.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appConfig.updateUserLoginStatus(false);
                appConfig.saveIsRememberLoginClicked(false);
                if(appConfig.isLoginUsingGmail()){
                    oneTapClient.signOut();
                    appConfig.saveLoginUsingGmail(false);
                }
                try {
                    DbHelper dbHelper = new DbHelper(context);
                    SQLiteDatabase database = dbHelper.getReadableDatabase();
                    dbHelper.clearAllTables(database);
                    dbHelper.close();
                } catch (Exception e){
                    Toast.makeText(context,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                //Move to login activity
                Intent intent = new Intent(context, Login.class);
                startActivity(intent);
            }
        });
        binding.lvOptProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    Profile_Option selectedOption = (Profile_Option) profileAdapter.getItem(position);
                    dialogChangeName(selectedOption, position);
                }
                else if(position == 1) {
                    Profile_Option selectedOption = (Profile_Option) profileAdapter.getItem(position);
                    dialogChangePassword(selectedOption, position);
                }
                else if(position == 2) {

                } else {

                }
            }
        });
//        textViewLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                appConfig.updateUserLoginStatus(false);
//                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            appConfig.saveLoginUsingGmail(false);
//
//                            // Move to login activity
//                            Intent intent = new Intent(requireContext(), Login.class);
//                            startActivity(intent);
//                            requireActivity().finish();
//                        } else {
//                            // Handle sign-out failure
//                        }
//                    }
//            }
//        });

        return view;
    }
    public void dialogChangeName(final Profile_Option item, int position) {
        final Dialog dialog = new Dialog(getContext());
        // Gán layout cho Dialog
        @NonNull DialogChangeNameBinding bindingChangeName = DialogChangeNameBinding.inflate(getLayoutInflater());
        View viewChangeName = bindingChangeName.getRoot();
        dialog.setContentView(viewChangeName);
        // Cấu hình kích thước Dialog
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            // Lấy kích thước màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //int dialogWidth = (int) (displayMetrics.widthPixels * 0.8);
            //int dialogHeight = (int) (displayMetrics.heightPixels * 0.35);
            // Đặt kích thước cho Dialog
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        bindingChangeName.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bindingChangeName.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Xử lý đổi tên



                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void dialogChangePassword(final Profile_Option item, int position) {
        final Dialog dialog = new Dialog(getContext());
        // Gán layout cho Dialog
        @NonNull DialogChangePasswordBinding bindingChangePassword = DialogChangePasswordBinding.inflate(getLayoutInflater());
        View viewChangePassword = bindingChangePassword.getRoot();
        dialog.setContentView(viewChangePassword);
        // Cấu hình kích thước Dialog
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            // Lấy kích thước màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //int dialogWidth = (int) (displayMetrics.widthPixels * 0.8);
            //int dialogHeight = (int) (displayMetrics.heightPixels * 0.35);
            // Đặt kích thước cho Dialog
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        bindingChangePassword.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bindingChangePassword.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Xử lý đổi mật khẩu


                dialog.dismiss();
            }
        });

        dialog.show();
    }
}