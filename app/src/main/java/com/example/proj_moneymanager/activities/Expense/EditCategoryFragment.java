package com.example.proj_moneymanager.activities.Expense;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.DialogBillEditBinding;
import com.example.proj_moneymanager.databinding.DialogEditCategoryBinding;
import com.example.proj_moneymanager.databinding.FragmentEditCategoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditCategoryFragment extends Fragment implements
        EditCategoryAdapter.OnEditCategoryClickListener {
    FragmentEditCategoryBinding binding;
    private EditCategoryAdapter editCategoryAdapter;
    private BroadcastReceiver broadcastReceiver;
    static ArrayList<Category> arrayListCategory = new ArrayList<Category>();
    long  UserID;
    String Color, Icon;
    public EditCategoryFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UserID = getArguments().getLong("UserID", 0);
        binding = FragmentEditCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        editCategoryAdapter = new EditCategoryAdapter(getContext(), this, arrayListCategory);
        GridView gridView = binding.gridviewEditcategory;
        gridView.setAdapter(editCategoryAdapter);

        readCategories(getContext());

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readCategories(getContext());
            }
        };

        return view;
    }
    @Override
    public void onEditCategoryClick(Category categoryItem, int position) {
        dialogEditCategory(categoryItem, position);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(DbContract.UI_UPDATE_BROADCAST));}
    }
    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }
    public static void readCategories(Context context) {
        readCategoryFromLocalStorage readCategoryTask = new readCategoryFromLocalStorage(context, arrayListCategory);
        readCategoryTask.execute();
    }
    public void dialogEditCategory(final Category categoryItem, int position) {
        final Dialog dialog = new Dialog(getContext());

        @NonNull DialogEditCategoryBinding binding = DialogEditCategoryBinding.inflate(LayoutInflater.from(getContext()));
        View viewDialogEdit = binding.getRoot();

        binding.edittextNameCategory.setText(categoryItem.getName());

        GridView gridviewColor = binding.gridviewColor;
        ColorAdapter colorAdapter = new ColorAdapter(getContext());
        gridviewColor.setAdapter(colorAdapter);
        binding.gridviewColor.setSelection(colorAdapter.getPositionByResourceName(categoryItem.getColor()));
        colorAdapter.setSelectedPosition(colorAdapter.getPositionByResourceName(categoryItem.getColor()));
        colorAdapter.setSelectedColorResourceName(categoryItem.getColor());

        GridView gridviewIcon = binding.gridviewIcon;
        IconAdapter iconAdapter =new IconAdapter(getContext());
        gridviewIcon.setAdapter(iconAdapter);
        binding.gridviewIcon.setSelection(iconAdapter.getPositionByResourceName(categoryItem.getIcon()));
        iconAdapter.setSelectedPosition(iconAdapter.getPositionByResourceName(categoryItem.getIcon()));
        iconAdapter.setSelectedIconResourceName(categoryItem.getIcon());

        dialog.setContentView(viewDialogEdit);

        Window window = dialog.getWindow();
        if (window != null) {
            // Cấu hình Dialog để hiển thị full screen và mờ đằng sau
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.7f; // Giả sử bạn muốn mức độ dim là 70%
            window.setAttributes(params);
        }

        binding.btnCancelCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.btnSaveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryItem.setIcon(iconAdapter.getSelectedIconResourceName());
                categoryItem.setColor(colorAdapter.getSelectedColorResourceName());
                categoryItem.setName(binding.edittextNameCategory.getText().toString());
                if (categoryItem.getName() != null && categoryItem.getColor() != null && categoryItem.getIcon() != null) {
                    DbHelper dbHelper = new DbHelper(getContext());
                    if (dbHelper.isCategoryNameExists(categoryItem.getID(), categoryItem.getName(), categoryItem.getUserID())) {
                        // Nếu name đã tồn tại, thông báo lỗi và không thực hiện import
                        Toast.makeText(getContext(), getContext().getString(R.string.Category_name_already_exists), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        updateCategoryToServer(categoryItem);

                        // Notify the adapter that the data has changed

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(DbContract.UI_UPDATE_BROADCAST);
                                getContext().sendBroadcast(intent);
                            }
                        });
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                    dbHelper.close();
                } else {
                    // Xử lý trường hợp khi thiếu input
                    Toast.makeText(getContext(), getContext().getString(R.string.Please_enter_all_fields), Toast.LENGTH_SHORT).show();
                }
            }});
        binding.btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryToServer(categoryItem);

                // Notify the adapter that the data has changed

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DbContract.UI_UPDATE_BROADCAST);
                        getContext().sendBroadcast(intent);
                    }
                });
                // Dismiss the dialog
                dialog.dismiss();
            };
        });
        dialog.show();
    }
    private void updateCategoryToServer(Category categoryItem) {
        if (checkNetworkConnection()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCCATEGORY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response= jsonObject.getString("response");
                                if (Response.equals("OK")){
//                                    Toast.makeText(getContext(), "Insert: " + "Name: " + name + ", Icon: " + icon + ", Color: " + color, Toast.LENGTH_LONG).show();
                                    categoryItem.setSyncStatus(DbContract.SYNC_STATUS_OK);
                                    updateCategoryToLocalDatabaseFromApp(categoryItem);
                                    Toast.makeText(getContext(), getContext().getString(R.string.Saved), Toast.LENGTH_LONG).show();
                                }else {
                                    categoryItem.setSyncStatus(DbContract.SYNC_STATUS_FAILED);
                                    updateCategoryToLocalDatabaseFromApp(categoryItem);
                                    Toast.makeText(getContext(), getContext().getString(R.string.SavedOffline), Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "Error occurred during import Category";
                    categoryItem.setSyncStatus(DbContract.SYNC_STATUS_FAILED);
                    updateCategoryToLocalDatabaseFromApp(categoryItem);
                    if (error != null && error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("categoryID", categoryItem.getID());
                    params.put("userID", String.valueOf(categoryItem.getUserID()));
                    params.put("name", categoryItem.getName());
                    params.put("icon", categoryItem.getIcon());
                    params.put("color", categoryItem.getColor());
                    params.put("method", "UPDATE");
                    return params;
                }
            };
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
        else {
            updateCategoryToLocalDatabaseFromApp(categoryItem);
            Toast.makeText(getContext(), getContext().getString(R.string.No_network_connection_Import_failed), Toast.LENGTH_LONG).show();
        }
    }
    private void deleteCategoryToServer(Category categoryItem) {
        if (checkNetworkConnection()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_SYNCCATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String serverResponse = jsonObject.getString("response");
                            if (serverResponse.equals("OK")) {
                                categoryItem.setSyncStatus(DbContract.SYNC_STATUS_OK);
                                deleteCategoryToLocalDatabaseFromApp(categoryItem);
                                Toast.makeText(getContext(), getContext().getString(R.string.Deleted), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), getContext().getString(R.string.Failed) + serverResponse, Toast.LENGTH_LONG).show();
                                Log.d("Delete response error", serverResponse);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), getContext().getString(R.string.Failed) + error, Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("categoryID", categoryItem.getID());
                    params.put("userID", String.valueOf(categoryItem.getUserID()));
                    params.put("method", "DELETE");
                    return params;
                }
            };
            // Thêm yêu cầu vào hàng đợi của Volley
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
        else {
            Toast.makeText(getContext(), getContext().getString(R.string.NoNetwork), Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }
    private void updateCategoryToLocalDatabaseFromApp(Category categoryItem){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            dbHelper.updateCategoryById(categoryItem, database);
            readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(), arrayListCategory);
            readCategoryFromLocalStorage.execute();
            editCategoryAdapter = new EditCategoryAdapter(getContext(), this, arrayListCategory);
            binding.gridviewEditcategory.setAdapter(editCategoryAdapter);
        } finally {
            dbHelper.close();
        }
    }
    private void deleteCategoryToLocalDatabaseFromApp(Category categoryItem){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            dbHelper.deleteCategoryById(categoryItem.getID(), database);
            readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(), arrayListCategory);
            readCategoryFromLocalStorage.execute();
            editCategoryAdapter = new EditCategoryAdapter(getContext(), this, arrayListCategory);
            binding.gridviewEditcategory.setAdapter(editCategoryAdapter);
        } finally {
            dbHelper.close();
        }
    }
}