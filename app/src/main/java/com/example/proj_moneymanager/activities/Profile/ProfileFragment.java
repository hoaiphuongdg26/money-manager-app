package com.example.proj_moneymanager.activities.Profile;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Login;
import com.example.proj_moneymanager.app.AppConfig;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private static Context context;
    private AppConfig appConfig;
//    private Identity.SignInClient oneTapClient;
    private ListView lv_profileOption;
    private ArrayList<Profile_Option> arr_profileOption;
    private Button buttonLogout;
    private SignInClient oneTapClient;
    private GoogleSignInClient googleSignInClient;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment(context);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConfig = new AppConfig(requireContext());
//        googleSignInClient = GoogleSignIn.getClient(requireContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        lv_profileOption = view.findViewById(R.id.lv_optProfile);
        arr_profileOption = new ArrayList<>();

        arr_profileOption.add(new Profile_Option(getString(R.string.Name), "Group03", R.drawable.icon_person_profile));
        arr_profileOption.add(new Profile_Option(getString(R.string.Password), "********", R.drawable.icon_lock));
        arr_profileOption.add(new Profile_Option(getString(R.string.Switch_Account), "group03@gmail.com", R.drawable.icon_switch));
        arr_profileOption.add(new Profile_Option(getString(R.string.Notification), "", R.drawable.icon_notification_fill));

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
}