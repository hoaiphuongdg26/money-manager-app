package com.example.proj_moneymanager.activities.Expense;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.proj_moneymanager.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Activity myContext;
    List<Category_Option> arrCategoryOption;
    public CategoryAdapter(Activity context, List<Category_Option> categoryOptions){
        this.myContext = context;
        arrCategoryOption = categoryOptions;
    }

    @Override
    public int getCount() {
        return arrCategoryOption.size();
    }

    @Override
    public Object getItem(int position) {
        return arrCategoryOption.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.gv_item_category, null);
        if (convertView == null){
            convertView = LayoutInflater.from(myContext).inflate(R.layout.gv_item_category,parent, false);
        }

        //get item
        //Category_Option categoryOption = getItem(position);
        //get view
        return convertView;

    }
}
