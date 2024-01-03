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

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private Activity myContext;
    private ArrayList<Category> arrCategory;
    private int selectedPosition = -1;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(myContext).inflate(R.layout.gv_item_category, parent, false);
        }
        // Find views in the layout
        TextView nameTextView = convertView.findViewById(R.id.textview_nameCategory);
        ImageButton iconImageButton = convertView.findViewById(R.id.imagebutton_iconCategory);
        LinearLayout colorLinearLayout = convertView.findViewById(R.id.linearlayout_color);
        FrameLayout frameLayout = convertView.findViewById(R.id.item_category);

        // Get the Category object for the current position
        Category category = arrCategory.get(position);

        // Set name
        nameTextView.setText(category.getName());

        // Set icon
        if (category.getIcon() != null) {
            int iconResourceId = getResourceId(myContext, category.getIcon());
            iconImageButton.setImageResource(iconResourceId);
        } else {
            // Provide a default icon or handle it accordingly
            iconImageButton.setImageResource(R.drawable.ic_question);
        }

        // Set color
        if (category.getColor() != null) {
            int colorResourceId = getResourceId(myContext, category.getColor());
            colorLinearLayout.setBackgroundResource(colorResourceId);
        } else {
            // Provide a default color or handle it accordingly
            colorLinearLayout.setBackgroundResource(R.drawable.colorbutton_default);
        }

        // Handle click on FrameLayout
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set selected position and notify data set changed
                setSelectedPosition(position);
                notifyDataSetChanged();

                // Save the selected category ID
                int selectedCategoryId = arrCategory.get(position).getID(); // Replace with your actual method to get the category ID

                // Notify the listener (ExpenseFragment) about the selected category ID
                if (categoryClickListener != null) {
                    categoryClickListener.onCategoryClick(selectedCategoryId);
//                    Toast.makeText(context, selectedCategoryId, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Highlight selected button
        frameLayout.setSelected(selectedPosition == position);
        return convertView;
    }
    private int getResourceId(Context context, String resourceName) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < arrCategory.size()) {
            selectedPosition = position;
            notifyDataSetChanged();
        }// Refresh GridView to update selected item
    }
    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }
    private OnCategoryClickListener categoryClickListener;
    public CategoryAdapter(ExpenseFragment context, OnCategoryClickListener categoryClickListener, ArrayList<Category> categoryOptions) {
        this.context = context.requireContext();
        this.myContext = context.requireActivity();
        this.categoryClickListener = categoryClickListener;
        arrCategory = categoryOptions;
    }

}
