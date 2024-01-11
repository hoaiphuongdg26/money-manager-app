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

public class EditCategoryFragment extends Fragment implements
        EditCategoryAdapter.OnEditCategoryClickListener,
        ColorAdapter.OnColorClickListener,
        IconAdapter.OnIconClickListener {
    FragmentEditCategoryBinding binding;
    private static EditCategoryAdapter editCategoryAdapter;
    private BroadcastReceiver broadcastReceiver;
    static ArrayList<Category> arrayListCategory = new ArrayList<Category>();
    long  UserID;
    String CategoryID, CategoryName, Color, Icon;
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
    public void onColorClick(String colorDescription) {
        Color = colorDescription;
    }

    @Override
    public void onIconClick(String iconDescription) {
        Icon = iconDescription;
    }
    @Override
    public void onEditCategoryClick(String categoryId) {
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
        if (editCategoryAdapter == null) {
            // Handle null adapter
            return;
        }

        readCategoryFromLocalStorage readCategoryTask = new readCategoryFromLocalStorage(context, arrayListCategory);
        readCategoryTask.setTaskListener(new readCategoryFromLocalStorage.TaskListener() {
            @Override
            public void onFinished() {
                // Dữ liệu đã được cập nhật, thông báo adapter
                if (editCategoryAdapter != null) {
                    editCategoryAdapter.notifyDataSetChanged();
                }
            }
        });

        if (readCategoryTask.getStatus() == AsyncTask.Status.PENDING) {
            // Execute the task only if it hasn't started yet
            readCategoryTask.execute();
        }
    }

}