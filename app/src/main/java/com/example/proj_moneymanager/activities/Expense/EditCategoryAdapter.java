package com.example.proj_moneymanager.activities.Expense;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.DialogEditCategoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCategoryAdapter extends BaseAdapter implements ColorAdapter.OnColorClickListener, IconAdapter.OnIconClickListener {
    private Context context;
    private Activity myContext;
    private ArrayList<Category> arrCategory;
    private EditCategoryFragment editCategoryFragment;
    private long selectedPosition = -1;
    private OnEditCategoryClickListener editCategoryClickListener;
    private BroadcastReceiver broadcastReceiver;
    Category categoryItem;

    public EditCategoryAdapter(Context context, BroadcastReceiver broadcastReceiver, ArrayList<Category> arrayListCategory) {
        this.context = context;
        this.editCategoryClickListener = editCategoryFragment;
        arrCategory = arrayListCategory;
    }

    public Activity getContext() {
        return myContext;
    }

    public EditCategoryAdapter(Context context, OnEditCategoryClickListener editCategoryClickListener, ArrayList<Category> categoryOptions) {
        this.context = context;
        this.myContext = (Activity) context;
        this.editCategoryClickListener = editCategoryClickListener;
        arrCategory = categoryOptions;
    }
    @Override
    public int getCount() {
        return arrCategory != null ? arrCategory.size() : 0;
    }
    @Override
    public Object getItem(int position) {
        return arrCategory.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public String getSelectedId(int position) {
        if (position >= 0 && position < arrCategory.size()) {
            return arrCategory.get(position).getID();
        }
        return "";
    }
    @Override
    public void onColorClick(String colorDescription) {

        categoryItem.setColor(colorDescription);
    }
    @Override
    public void onIconClick(String iconDescription) {

        categoryItem.setIcon(iconDescription);
    }
    static class ViewHolder {
        TextView nameTextView;
        ImageButton iconImageButton;
        FrameLayout colorframeLayout;
        LinearLayout borderLinearLayout;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gv_item_category, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.textview_nameCategory);
            viewHolder.iconImageButton = convertView.findViewById(R.id.imagebutton_iconCategory);
            viewHolder.borderLinearLayout = convertView.findViewById(R.id.linearlayout_border);
            viewHolder.colorframeLayout = convertView.findViewById(R.id.framelayout_colorCategory);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the Category object for the current position
        Category category = arrCategory.get(position);

        // Set name
        viewHolder.nameTextView.setText(category.getName());

        // Set icon
        if (category.getIcon() != null) {
            int iconResourceId = getResourceId(myContext, category.getIcon());
            viewHolder.iconImageButton.setImageResource(iconResourceId);
        } else {
            // Provide a default icon or handle it accordingly
            viewHolder.iconImageButton.setImageResource(R.drawable.ic_question);
        }

        // Set color
        if (category.getColor() != null) {
            int colorResourceId = getResourceId(myContext, category.getColor());
            viewHolder.colorframeLayout.setBackgroundResource(colorResourceId);
        } else {
            // Provide a default color or handle it accordingly
            viewHolder.colorframeLayout.setBackgroundResource(R.drawable.colorbutton_default);
        }

        // Handle click on FrameLayout
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set selected position and notify data set changed
                setSelectedPosition(position);
                notifyDataSetChanged();

                // Save the selected category ID
                String selectedCategoryId = getSelectedId(position);

                // Notify the listener (ExpenseFragment) about the selected category ID
                if (editCategoryClickListener != null) {
                    editCategoryClickListener.onEditCategoryClick(arrCategory.get(position), position);
                    // Use context to show the dialog
                    //dialogEditCategory(selectedCategoryId);
                }
            }
        };
        // Set click listener for all relevant views
        viewHolder.borderLinearLayout.setOnClickListener(clickListener);
        viewHolder.colorframeLayout.setOnClickListener(clickListener);
        viewHolder.iconImageButton.setOnClickListener(clickListener);
        viewHolder.nameTextView.setOnClickListener(clickListener);

        // Highlight selected button
        viewHolder.borderLinearLayout.setSelected(selectedPosition == position);
        return convertView;
    }
    private int getResourceId(Context context, String resourceName) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }
    public void setSelectedPosition(long position) {
        if (position >= 0 && position < arrCategory.size()) {
            selectedPosition = position;
            notifyDataSetChanged();
        }// Refresh GridView to update selected item
    }
    public interface OnEditCategoryClickListener {
        void onEditCategoryClick(Category categoryItem, int position);
    }
    public EditCategoryAdapter(EditCategoryFragment context, OnEditCategoryClickListener editCategoryClickListener, ArrayList<Category> categoryOptions) {
        this.context = context.requireContext();
        this.myContext = context.requireActivity();
        this.editCategoryClickListener = editCategoryClickListener;
        arrCategory = categoryOptions;
    }


}
