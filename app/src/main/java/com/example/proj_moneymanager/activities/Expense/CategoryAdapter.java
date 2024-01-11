package com.example.proj_moneymanager.activities.Expense;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Plan.CalendarFragment;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private Activity myContext;
    private ArrayList<Category> arrCategory;
    private long selectedPosition = -1;
    private Category categorySelectedListener;
    public CategoryAdapter(Activity context, ArrayList<Category> categoryOptions){
        this.myContext = context;
        arrCategory = categoryOptions;
    }
    public CategoryAdapter(ExpenseFragment context) {
        this.context = context.requireContext();
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
        return null;
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
            convertView = LayoutInflater.from(myContext).inflate(R.layout.gv_item_category, parent, false);
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
                if (categoryClickListener != null) {
                    categoryClickListener.onCategoryClick(selectedCategoryId);
//                     Toast.makeText(context, (int) selectedCategoryId, Toast.LENGTH_LONG).show();
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
    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryId);
    }
    private OnCategoryClickListener categoryClickListener;
    public CategoryAdapter(ExpenseFragment context, OnCategoryClickListener categoryClickListener, ArrayList<Category> categoryOptions) {
        this.context = context.requireContext();
        this.myContext = context.requireActivity();
        this.categoryClickListener = categoryClickListener;
        arrCategory = categoryOptions;
    }
    public CategoryAdapter(CalendarFragment context, OnCategoryClickListener categoryClickListener, ArrayList<Category> categoryOptions) {
        this.context = context.requireContext();
        this.myContext = context.requireActivity();
        this.categoryClickListener = categoryClickListener;
        arrCategory = categoryOptions;
    }
}
