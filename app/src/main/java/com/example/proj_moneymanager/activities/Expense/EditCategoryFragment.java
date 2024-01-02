package com.example.proj_moneymanager.activities.Expense;

import android.database.Cursor;
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
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.FragmentEditCategoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCategoryFragment extends Fragment implements ColorAdapter.OnColorClickListener, IconAdapter.OnIconClickListener{
    FragmentEditCategoryBinding binding;
    ArrayList<Category> arrayListCategory = new ArrayList<Category>();
    TextView nameCategory;
    ImageButton iconCategory, colorCategory;
    private int selectedColorDrawable;
    Button Import;
    ColorAdapter colorAdapter;
    IconAdapter iconAdapter;
    String color, icon;
    public EditCategoryFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditCategoryBinding.inflate(getLayoutInflater());
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
             insertCategoryToServer(name);
        } else {
            // Xử lý trường hợp khi thiếu input
        }
    }
    private void insertCategoryToServer(String name) {
        if (checkNetworkConnection()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCCATEGORY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response= jsonObject.getString("response");
                                if (Response.equals("OK")){
                                    insertCategoryToLocalDatabaseFromApp(name, icon, color, DbContract.SYNC_STATUS_OK);
                                    Toast.makeText(getContext(), "Import successful", Toast.LENGTH_LONG).show();
                                }else {
                                    insertCategoryToLocalDatabaseFromApp(name, icon, color, DbContract.SYNC_STATUS_FAILED);
                                    Toast.makeText(getContext(), "Import failed", Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    insertCategoryToLocalDatabaseFromApp(name, icon, color, DbContract.SYNC_STATUS_FAILED);
                    Toast.makeText(getContext(), "Error occurred during import", Toast.LENGTH_LONG).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("icon", icon);
                    params.put("color", color);
                    return params;
                }
            };
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
        else {
            insertCategoryToLocalDatabaseFromApp(name, icon, color, DbContract.SYNC_STATUS_FAILED);
            Toast.makeText(getContext(), "No network connection. Import failed.", Toast.LENGTH_LONG).show();
        }

    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }
    private long insertCategoryToLocalDatabaseFromApp(String name, String icon, String color, int syncstatus){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long categoryID = dbHelper.insertCategoryToLocalDatabaseFromApp(name, icon, color, syncstatus, database);
        readFromLocalStorage();
        dbHelper.close();
        return categoryID;
    }
    private void readFromLocalStorage() {
        arrayListCategory.clear();
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readCategoryFromLocalDatabase(database);

        int columnIndexCategoryID = cursor.getColumnIndex(DbContract.CategoryEntry._ID);
        int columnIndexName = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_NAME);
        int columnIndexIcon = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_ICON);
        int columnIndexColor = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_COLOR);
        int columnIndexSyncStatus = cursor.getColumnIndex(DbContract.CategoryEntry.COLUMN_SYNC_STATUS);

        while (cursor.moveToNext()) {
            // Check if the column indices are valid before accessing the values
            if (columnIndexCategoryID != -1 && columnIndexName != -1 &&
                    columnIndexColor != -1 && columnIndexSyncStatus != -1) {

                int categoryID = cursor.getInt(columnIndexCategoryID);
                String name = cursor.getString(columnIndexName);
                String icon = cursor.getString(columnIndexIcon);
                String color = cursor.getString(columnIndexColor);
                int syncStatus = cursor.getInt(columnIndexSyncStatus);

//                 Create a new Category object with all required parameters
                Category category = new Category(categoryID, name, icon, color, syncStatus);
                arrayListCategory.add(category);
            } else {
                // Handle the case where the column indices are not found
                // You may log an error, throw an exception, or handle it in some way
            }
        }
        cursor.close();
        dbHelper.close();
    }

    @Override
    public void onColorClick(String colorDescription) {
        Toast.makeText(getContext(), "Resource Name: " + colorDescription, Toast.LENGTH_LONG).show();
        color = colorDescription;
    }

    @Override
    public void onIconClick(String iconDescription) {
        Toast.makeText(getContext(), "Resource Name: " + iconDescription, Toast.LENGTH_LONG).show();
        icon = iconDescription;
    }
}