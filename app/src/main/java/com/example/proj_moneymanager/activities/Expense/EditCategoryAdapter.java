package com.example.proj_moneymanager.activities.Expense;

import static android.provider.Settings.System.getString;
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
import com.example.proj_moneymanager.AsyncTasks.readCategoryFromLocalStorage;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbContract;
import com.example.proj_moneymanager.database.DbHelper;
import com.example.proj_moneymanager.database.MySingleton;
import com.example.proj_moneymanager.database.NetworkMonitor;
import com.example.proj_moneymanager.databinding.DialogEditCategoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
                    editCategoryClickListener.onEditCategoryClick(selectedCategoryId);
                    // Use context to show the dialog
                    showEditDialog(selectedCategoryId);
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
        void onEditCategoryClick(String categoryId);
    }
    public EditCategoryAdapter(EditCategoryFragment context, OnEditCategoryClickListener editCategoryClickListener, ArrayList<Category> categoryOptions) {
        this.context = context.requireContext();
        this.myContext = context.requireActivity();
        this.editCategoryClickListener = editCategoryClickListener;
        arrCategory = categoryOptions;
    }
    public void showEditDialog(String categoryID) {
        final Dialog dialog = new Dialog(context);

        @NonNull DialogEditCategoryBinding binding = DialogEditCategoryBinding.inflate(LayoutInflater.from(context));
        View viewDialogEdit = binding.getRoot();

        // Set thông tin của bill vào dialog để chỉnh sửa
        DbHelper dbHelper= new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        categoryItem = dbHelper.getItemCategory(categoryID,database);
        dbHelper.close();
        binding.edittextNameCategory.setText(categoryItem.getName());

        GridView gridviewColor = binding.gridviewColor;
        ColorAdapter colorAdapter = new ColorAdapter(this);
        gridviewColor.setAdapter(colorAdapter);
        binding.gridviewColor.setSelection(colorAdapter.getPositionByResourceName(categoryItem.getColor()));
        colorAdapter.setSelectedPosition(colorAdapter.getPositionByResourceName(categoryItem.getColor()));

        GridView gridviewIcon = binding.gridviewIcon;
        IconAdapter iconAdapter =new IconAdapter(this);
        gridviewIcon.setAdapter(iconAdapter);
        binding.gridviewIcon.setSelection(iconAdapter.getPositionByResourceName(categoryItem.getIcon()));
        iconAdapter.setSelectedPosition(iconAdapter.getPositionByResourceName(categoryItem.getIcon()));

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
                categoryItem.setName(binding.edittextNameCategory.getText().toString());
                if (categoryItem.getName() != null && categoryItem.getColor() != null && categoryItem.getIcon() != null) {
                    DbHelper dbHelper = new DbHelper(getContext());
                    if (dbHelper.isCategoryNameExists(categoryItem.getID(), categoryItem.getName(), categoryItem.getUserID())) {
                        // Nếu name đã tồn tại, thông báo lỗi và không thực hiện import
                        Toast.makeText(context, context.getString(R.string.Category_name_already_exists), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        updateCategoryToServer(categoryItem);

                        // Notify the adapter that the data has changed

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                Intent intent = new Intent(DbContract.UI_UPDATE_BROADCAST);
                                context.sendBroadcast(intent);
                            }
                        });
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                    dbHelper.close();
                } else {
                    // Xử lý trường hợp khi thiếu input
                    Toast.makeText(getContext(), context.getString(R.string.Please_enter_all_fields), Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
            }});
        binding.btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryToServer(categoryItem);

                // Notify the adapter that the data has changed

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        Intent intent = new Intent(DbContract.UI_UPDATE_BROADCAST);
                        context.sendBroadcast(intent);
                    }
                });
                // Dismiss the dialog
                dialog.dismiss();
            };
        });
        dialog.show();
    }
    private void updateCategoryToServer(Category categoryItem) {
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
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
                                    dbHelper.updateCategoryById(categoryItem, database);
                                    Toast.makeText(context, context.getString(R.string.Saved), Toast.LENGTH_LONG).show();
                                }else {
                                    categoryItem.setSyncStatus(DbContract.SYNC_STATUS_FAILED);
                                    dbHelper.updateCategoryById(categoryItem, database);
                                    Toast.makeText(context, context.getString(R.string.SavedOffline), Toast.LENGTH_LONG).show();
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
                    categoryItem.setSyncStatus(DbContract.SYNC_STATUS_FAILED);
                    dbHelper.updateCategoryById(categoryItem, database);
                    if (error != null && error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    dbHelper.close();
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
            categoryItem.setSyncStatus(DbContract.SYNC_STATUS_FAILED);
            dbHelper.updateCategoryById(categoryItem, database);
            Toast.makeText(context, context.getString(R.string.No_network_connection_Import_failed), Toast.LENGTH_LONG).show();
        }
    }
    private void deleteCategoryToServer(Category categoryItem) {
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
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
                                    dbHelper.deleteCategoryById(categoryItem.getID(), database);
                                    Toast.makeText(getContext(), context.getString(R.string.Deleted), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), context.getString(R.string.Failed) + serverResponse, Toast.LENGTH_LONG).show();
                                    Log.d("Delete response error", serverResponse);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                dbHelper.close();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), context.getString(R.string.Failed) + error, Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, context.getString(R.string.NoNetwork), Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkNetworkConnection() {
        return NetworkMonitor.checkNetworkConnection(getContext());
    }

}
