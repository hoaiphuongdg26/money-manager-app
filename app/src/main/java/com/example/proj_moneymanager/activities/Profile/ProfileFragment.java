package com.example.proj_moneymanager.activities.Profile;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.Object.UserInformation;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.databinding.DialogChangeNameBinding;
import com.example.proj_moneymanager.databinding.DialogChangePasswordBinding;
import com.example.proj_moneymanager.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    boolean isCurrentPwdCorrect = false;
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
        lv_profileOption = view.findViewById(R.id.lv_optProfile);
        appConfig = new AppConfig(getContext());
        arr_profileOption=new ArrayList<>();

        readUserDataFromLocalStorageTask readUserDataFromLocalStorageTask = new readUserDataFromLocalStorageTask(context, arr_profileOption);
        readUserDataFromLocalStorageTask.execute();

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
                if(bindingChangeName.edittextEnterNewName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(),getString(R.string.Please_enter_new_name), Toast.LENGTH_LONG).show();
                    return;
                }
                //fetch data mới lên remote db
                DbHelper dbHelper = new DbHelper(getContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                userInformation = new UserInformation();
                userInformation.setUserID(getArguments().getLong("UserID", 0));
                ContentValues values = new ContentValues();
                values.put("Fullname", String.valueOf(bindingChangeName.edittextEnterNewName.getText().toString()));
                String whereClause = DbContract.UserInformationEntry._ID + "=?";
                String[] whereArgs = new String[]{
                        String.valueOf(userInformation.getUserID())
                };
                // Thực hiện cập nhật dữ liệu vào local db
                database.update(DbContract.UserInformationEntry.TABLE_NAME, values, whereClause, whereArgs);
                //Sau khi cập nhật dữ liệu, đọc lại dữ liệu từ cơ sở dữ liệu và cập nhật lại ListView
                readUserDataFromLocalStorageTask readUserDataFromLocalStorageTask = new readUserDataFromLocalStorageTask(context, arr_profileOption);
                readUserDataFromLocalStorageTask.execute();
                ProfileAdapter profileAdapter = new ProfileAdapter(
                        requireActivity(),
                        arr_profileOption
                );
                lv_profileOption.setAdapter(profileAdapter);

                Cursor cursor = dbHelper.getUserInformation(userInformation.getUserID(),database);
                if(cursor.moveToFirst()) {
                    int columnIndexUserFullname = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_FULL_NAME);
                    int columnIndexUserName = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_USERNAME);
                    int columnIndexUserPassword = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_PASSWORD);
                    int columnIndexEmail = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_EMAIL);

                    String fullName = cursor.getString(columnIndexUserFullname);
                    String userName = cursor.getString(columnIndexUserName);
                    String password = cursor.getString(columnIndexUserPassword);
                    String email = cursor.getString(columnIndexEmail);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCPROFILE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String serverResponse = jsonObject.getString("response");
                                        if (serverResponse.equals("OK")) {
                                            Toast.makeText(getContext(), getString(R.string.Fullname_change_successfully), Toast.LENGTH_LONG).show();
                                        } else {
                                            //neu server tra về "fail"
                                            Toast.makeText(getContext(), serverResponse, Toast.LENGTH_LONG).show();
                                            Log.d("Update response error", serverResponse);
                                            //khong lam gì cả
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error appropriately (e.g., log or notify the user)
                            Toast.makeText(getContext(), "Fail to sync data", Toast.LENGTH_LONG);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("_password", password);
                            params.put("userID", String.valueOf(userInformation.getUserID()));
                            params.put("fullName", String.valueOf(bindingChangeName.edittextEnterNewName.getText().toString()));
                            params.put("userName", userName);
                            //params.put("email", email);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                }


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
        bindingChangePassword.edittextEnterOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String currentPasswrd = arr_profileOption.get(1).getLabelInfo();
                if(!hasFocus) {
                if(!bindingChangePassword.edittextEnterOldPassword.getText().toString().equals(currentPasswrd)) {
                    bindingChangePassword.textviewLabelErrorOldPasswd.setText(getString(R.string.Wrong_Password));
                    isCurrentPwdCorrect = false;
                    bindingChangePassword.buttonSave.setEnabled(false);
                }
                else {
                    bindingChangePassword.textviewLabelErrorOldPasswd.setText(null);
                    isCurrentPwdCorrect = true;
                    bindingChangePassword.buttonSave.setEnabled(true);
                    }
                }
                else {
                    bindingChangePassword.textviewLabelErrorOldPasswd.setText(null);
                    if(!bindingChangePassword.edittextEnterOldPassword.getText().toString().equals(currentPasswrd)) {
                        bindingChangePassword.edittextEnterOldPassword.setText(null);
                    }
                }
            }
        });
        bindingChangePassword.edittextEnterNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    bindingChangePassword.textviewLabelErrorConfirmPasswd.setText(null);
                }
            }
        });
        bindingChangePassword.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bindingChangePassword.edittextEnterOldPassword.getText().toString().isEmpty()|| bindingChangePassword.edittextEnterNewPassword.getText().toString().isEmpty() || bindingChangePassword.edittextConfirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.Please_enter_all_fields), Toast.LENGTH_LONG).show();
                    return;
                }
                if (bindingChangePassword.edittextConfirmPassword.getText().toString().equals(bindingChangePassword.edittextEnterNewPassword.getText().toString())) {
                    //Xử lý đổi mật khẩu
                    //fetch data mới lên remote db
                    DbHelper dbHelper = new DbHelper(getContext());
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    userInformation = new UserInformation();
                    userInformation.setUserID(getArguments().getLong("UserID", 0));
                    ContentValues values = new ContentValues();
                    values.put("Password", String.valueOf(bindingChangePassword.edittextConfirmPassword.getText().toString()));
                    String whereClause = DbContract.UserInformationEntry._ID + "=?";
                    String[] whereArgs = new String[]{
                            String.valueOf(userInformation.getUserID())
                    };
                    // Thực hiện cập nhật dữ liệu vào local db
                    database.update(DbContract.UserInformationEntry.TABLE_NAME, values, whereClause, whereArgs);
                    //Sau khi cập nhật dữ liệu, đọc lại dữ liệu từ cơ sở dữ liệu và cập nhật lại ListView
                    readUserDataFromLocalStorageTask readUserDataFromLocalStorageTask = new readUserDataFromLocalStorageTask(context, arr_profileOption);
                    readUserDataFromLocalStorageTask.execute();
                    ProfileAdapter profileAdapter = new ProfileAdapter(
                            requireActivity(),
                            arr_profileOption
                    );
                    lv_profileOption.setAdapter(profileAdapter);

                    Cursor cursor = dbHelper.getUserInformation(userInformation.getUserID(),database);
                    if(cursor.moveToFirst()) {
                        int columnIndexUserFullname = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_FULL_NAME);
                        int columnIndexUserName = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_USERNAME);
                        int columnIndexUserPassword = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_PASSWORD);
                        int columnIndexEmail = cursor.getColumnIndex(DbContract.UserInformationEntry.COLUMN_EMAIL);

                        String fullName = cursor.getString(columnIndexUserFullname);
                        String userName = cursor.getString(columnIndexUserName);
                        String password = cursor.getString(columnIndexUserPassword);
                        String email = cursor.getString(columnIndexEmail);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCPROFILE,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String serverResponse = jsonObject.getString("response");
                                            if (serverResponse.equals("OK")) {
                                                Toast.makeText(getContext(), getString(R.string.Password_change_successfully), Toast.LENGTH_LONG).show();
                                                appConfig.saveUserPassword(bindingChangePassword.edittextConfirmPassword.getText().toString());
                                            } else {
                                                //neu server tra về "fail"
                                                Toast.makeText(getContext(), serverResponse, Toast.LENGTH_LONG).show();
                                                Log.d("Update response error", serverResponse);
                                                //khong lam gì cả
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error appropriately (e.g., log or notify the user)
                                Toast.makeText(getContext(), "Fail to sync data", Toast.LENGTH_LONG);
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("_password", String.valueOf(bindingChangePassword.edittextConfirmPassword.getText().toString()));
                                params.put("userID", String.valueOf(userInformation.getUserID()));
                                params.put("fullName", fullName);
                                params.put("userName", userName);
                                //params.put("email", email);
                                return params;
                            }
                        };
                        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                    }
                }
                else {
                    bindingChangePassword.textviewLabelErrorConfirmPasswd.setText(getString(R.string.Password_confirm_not_match));
                    bindingChangePassword.edittextEnterNewPassword.setText(null);
                    bindingChangePassword.edittextConfirmPassword.setText(null);
                    return;
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    class readUserDataFromLocalStorageTask extends AsyncTask<Void, Void, ArrayList<Profile_Option>> {

        private final ArrayList<Profile_Option> arrayListUser;
        private Context context;
        readUserDataFromLocalStorageTask(Context context, ArrayList<Profile_Option> arrayListUser) {
            this.context = context;
            this.arrayListUser = arrayListUser;
        }
        @Override
        protected void onPostExecute(ArrayList<Profile_Option> arrResult) {
            arrResult = arrayListUser;
            super.onPostExecute(arrResult);
        }

        @Override
        protected ArrayList<Profile_Option> doInBackground(Void ... voids) {
            //arr_profileOption = new ArrayList<Profile_Option>();
            //if(arrayListUser != null) {arrayListUser.clear();} // Xoá dữ liệu hiện tại để ập nhật lại từ đầu
            arrayListUser.clear();

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

                //arr_profileOption = new ArrayList<Profile_Option>();
                // Thêm vào danh sách
                arrayListUser.add(new Profile_Option(getString(R.string.Name), fullName, R.drawable.icon_person_profile));
                arrayListUser.add(new Profile_Option(getString(R.string.Password), password, R.drawable.icon_lock));
//                arrayListUser.add(new Profile_Option(getString(R.string.Email), email, R.drawable.icon_email_profile));
//                arrayListUser.add(new Profile_Option(getString(R.string.Notification), "", R.drawable.icon_notification_fill));
            }
            cursor.close();
            dbHelper.close();
            return arrayListUser;
        }
    }
}