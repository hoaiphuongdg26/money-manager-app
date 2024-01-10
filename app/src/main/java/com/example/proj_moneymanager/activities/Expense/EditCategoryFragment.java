package com.example.proj_moneymanager.activities.Expense;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class EditCategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    FragmentEditCategoryBinding binding;
    private CategoryAdapter categoryAdapter;
    private BroadcastReceiver broadcastReceiver;
    ArrayList<Category> arrayListCategory = new ArrayList<Category>();

    long  UserID;
    String CategoryID;
    String color, icon;
    public EditCategoryFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UserID = getArguments().getLong("UserID", 0);
        binding = FragmentEditCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        readCategoryFromLocalStorage readCategoryFromLocalStorageTask = new readCategoryFromLocalStorage(getContext(), arrayListCategory);
        readCategoryFromLocalStorageTask.execute();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //loading the again
                readCategoryFromLocalStorageTask.execute();
            }
        };
        categoryAdapter = new CategoryAdapter(this, (CategoryAdapter.OnCategoryClickListener) this, arrayListCategory);
        GridView gridView = binding.gridviewEditcategory;
        gridView.setAdapter(categoryAdapter);
        // Set item click listener for the GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category clickedCategory = arrayListCategory.get(position);
                showEditDialog(clickedCategory, position);
            }
        });
        return view;
    }
    public void showEditDialog(final Category categoryItem, int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle(getString(R.string.EDIT_CATEGORY));

        @NonNull DialogEditCategoryBinding binding = DialogEditCategoryBinding.inflate(getLayoutInflater());
        View viewDialogEdit = binding.getRoot();

        // Set thông tin của bill vào dialog để chỉnh sửa
        binding.edittextNameCategory.setText(categoryItem.getName());
        binding.gridviewIcon.setSelection(categoryItem.getIcon());
        binding.gridviewColor.setSelection(categoryItem.getColor());

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
                // Your code for updating the category
                String newName = binding.edittextNameCategory.getText().toString();
                int newIcon = binding.gridviewIcon.getSelectedItemPosition(); // Assuming you get the selected position for the icon
                int newColor = binding.gridviewColor.getSelectedItemPosition(); // Assuming you get the selected position for the color

                // Update the category in your local database
                updateCategoryInLocalDatabase(categoryItem.getID(), newName, newIcon, newColor);

                // Dismiss the dialog
                dialog.dismiss();

                // Notify the adapter that the data has changed
                categoryAdapter.notifyDataSetChanged();
            }});
        binding.btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(categoryItem.getID());

                // Dismiss the dialog
                dialog.dismiss();

                // Notify the adapter that the data has changed
                categoryAdapter.notifyDataSetChanged();
        })
        dialog.show();
        });
    }
    void updateCategoryInLocalDatabase(String categoryID, String name, String icon, String color, int syncstatus){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbHelper.updateCategoryById(categoryID, name, icon, color, syncstatus, database);
        readCategoryFromLocalStorage readCategoryFromLocalStorage = new readCategoryFromLocalStorage(getContext(),arrayListCategory);
        readCategoryFromLocalStorage.execute();
        dbHelper.close();
    }
}