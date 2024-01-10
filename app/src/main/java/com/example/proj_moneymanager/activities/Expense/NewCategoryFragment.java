package com.example.proj_moneymanager.activities.Expense;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.FragmentNewCategoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewCategoryFragment extends Fragment implements ColorAdapter.OnColorClickListener, IconAdapter.OnIconClickListener {
    FragmentNewCategoryBinding binding;
    ArrayList<Category> arrayListCategory = new ArrayList<Category>();
    TextView nameCategory;
    ImageButton iconCategory, colorCategory;
    private int selectedColorDrawable;
    Button Import;
    ColorAdapter colorAdapter;
    IconAdapter iconAdapter;
    long  UserID;
    String CategoryID;
    String color, icon;
    public NewCategoryFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UserID = getArguments().getLong("UserID", 0);
        binding = FragmentNewCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        nameCategory = binding.edittextNameCategory;

        GridView gridviewColor = binding.gridviewColor;
        colorAdapter = new ColorAdapter(this, this);
        gridviewColor.setAdapter(colorAdapter);

        GridView gridviewIcon = binding.gridviewIcon;
        iconAdapter =new IconAdapter(this, this);
        gridviewIcon.setAdapter(iconAdapter);

        Import = (Button) binding.btnImport;
        Import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi Button được click
                ImportCategory();
            }
        });
        return view;
    }
    private void ImportCategory(){
        // Lấy data
        String name = nameCategory.getText().toString();
        if (name != null && icon != null && color != null) {
            DbHelper dbHelper = new DbHelper(getContext());
            if (dbHelper.isCategoryNameExists(name, UserID)) {
                // Nếu name đã tồn tại, thông báo lỗi và không tiếp tục thực hiện import
                Toast.makeText(getContext(), getString(R.string.Category_name_already_exists), Toast.LENGTH_SHORT).show();
            }
            else {
                insertCategoryToServer(name);
                nameCategory.setText(""); // Xoá nội dung của EditText
                colorAdapter.setSelectedPosition(-1);
                iconAdapter.setSelectedPosition(-1);
            }
            dbHelper.close();
        } else {
            // Xử lý trường hợp khi thiếu input
            Toast.makeText(getContext(), getString(R.string.Please_enter_all_fields), Toast.LENGTH_SHORT).show();
        }
    }
    private void insertCategoryToServer(String name) {
        if (checkNetworkConnection()){
            CategoryID = insertCategoryToLocalDatabaseFromApp(UserID, name, icon, color, DbContract.SYNC_STATUS_PENDING);
            DbHelper dbHelper = new DbHelper(getContext());
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCCATEGORY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response= jsonObject.getString("response");
                                if (Response.equals("OK")){
                                    Toast.makeText(getContext(), "Insert: " + "Name: " + name + ", Icon: " + icon + ", Color: " + color, Toast.LENGTH_LONG).show();
                                    dbHelper.updateCategoryInLocalDatabase(CategoryID, DbContract.SYNC_STATUS_OK, database);
                                    Toast.makeText(getContext(), getString(R.string.Insert) + ": " + getString(R.string.Name)+": " + name + ", "+getString(R.string.Icon)+": " + icon + ", "+getString(R.string.Color)+": " + color, Toast.LENGTH_LONG).show();
//                                    Toast.makeText(getContext(), "Import successful", Toast.LENGTH_LONG).show();
                                }else {
                                    dbHelper.updateCategoryInLocalDatabase(CategoryID, DbContract.SYNC_STATUS_FAILED, database);
                                    Toast.makeText(getContext(),getString(R.string.Import_failed), Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            finally {
                                dbHelper.close();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "Error occurred during import Category";
                    dbHelper.updateCategoryInLocalDatabase(CategoryID, DbContract.SYNC_STATUS_FAILED, database);
                    if (error != null && error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    dbHelper.close();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("categoryID", String.valueOf(CategoryID));
                    params.put("userID", String.valueOf(UserID));
                    params.put("name", name);
                    params.put("icon", icon);
                    params.put("color", color);
                    return params;
                }
            };
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
        else {
            CategoryID = insertCategoryToLocalDatabaseFromApp(UserID, name, icon, color, DbContract.SYNC_STATUS_FAILED);
            Toast.makeText(getContext(), getString(R.string.No_network_connection_Import_failed), Toast.LENGTH_LONG).show();
        }

    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }
    private String insertCategoryToLocalDatabaseFromApp(long UserID, String name, String icon, String color, int syncstatus){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String categoryID = dbHelper.insertCategoryToLocalDatabaseFromApp(UserID, name, icon, color, syncstatus, database);
        readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(),arrayListCategory);
        readCategoryFromLocalStorage.execute();
        dbHelper.close();
        return categoryID;
    }
    @Override
    public void onColorClick(String colorDescription) {
        color = colorDescription;
//        Toast.makeText(getContext(),"Selected color: " + color, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onIconClick(String iconDescription) {
        icon = iconDescription;
//        Toast.makeText(getContext(),"Selected icon: " + icon, Toast.LENGTH_LONG).show();
    }
}