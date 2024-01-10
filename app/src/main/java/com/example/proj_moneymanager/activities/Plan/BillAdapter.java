package com.example.proj_moneymanager.activities.Plan;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.Object.Bill;
import com.example.proj_moneymanager.Object.Category;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.database.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class BillAdapter extends BaseAdapter {
    private Activity myContext;
    ArrayList<Bill> arrayListBill;
    private ArrayList<Category> arrayListCategory;
    public BillAdapter(Activity context, ArrayList<Bill> arrayListBill){
        this.myContext = context;
        this.arrayListBill = arrayListBill;
    }
    public BillAdapter(Activity context, ArrayList<Bill> arrayListBill, ArrayList<Category> arrayListCategory){
        this.myContext = context;
        this.arrayListBill = arrayListBill;
        this.arrayListCategory = arrayListCategory;
    }
    @Override
    public int getCount() {
        return arrayListBill.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListBill.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Đổ từng item trong list vào từng dòng
        convertView = inflater.inflate(R.layout.lv_item_history, null);
        if (convertView == null) {
            convertView = LayoutInflater.from(myContext).inflate(R.layout.lv_item_history, parent, false);
        }
        //get item
        //Bill bill =  arrayListBill.get(position);
        //get item
        Bill bill = (Bill) getItem(position);
        //get view
        TextView txt_Note = (TextView) convertView.findViewById(R.id.textview_noteForEachCategory);
        txt_Note.setText(arrayListBill.get(position).getNote());

        TextView txt_Price = (TextView) convertView.findViewById(R.id.textview_priceForEachCategory);
        txt_Price.setText(MainActivity.formatCurrency(arrayListBill.get(position).getMoney()));

        DbHelper dbHelper = new DbHelper(convertView.getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String categoryId = bill.getCategoryID();

        TextView txt_Category = (TextView) convertView.findViewById(R.id.textview_eachCategory);
        String categoryName = dbHelper.getCategoryNameById(categoryId, database);
        txt_Category.setText(categoryName);

        // Cái hình to nhất
        TextView txt_CategoryName = convertView.findViewById(R.id.textview_name);
        ImageButton imgButton_CategoryIcon = convertView.findViewById(R.id.imagebutton_icon);
        FrameLayout fl_CategoyColor = convertView.findViewById(R.id.framelayout_color);

        Category category = dbHelper.getItemCategory(categoryId, database);
        if (category != null) {
            // Set name
            txt_CategoryName.setText(category.getName());

            // Set icon
            if (category.getIcon() != null) {
                int iconResourceId = getResourceId(myContext, category.getIcon());
                imgButton_CategoryIcon.setImageResource(iconResourceId);
            } else {
                // Provide a default icon or handle it accordingly
                imgButton_CategoryIcon.setImageResource(R.drawable.ic_question);
            }

            // Set color
            if (category.getColor() != null) {
                int colorResourceId = getResourceId(myContext, category.getColor());
                fl_CategoyColor.setBackgroundResource(colorResourceId);
            } else {
                // Provide a default color or handle it accordingly
                fl_CategoyColor.setBackgroundResource(R.drawable.colorbutton_default);
            }
        }

        ImageView img_dotOffline = convertView.findViewById(R.id.imageview_dotOffline);
        if (arrayListBill.get(position).getSyncStatus() == 1 ) img_dotOffline.setVisibility(View.VISIBLE);

        // Set sự kiện click cho mỗi item trong danh sách
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBillItemClickListener != null) {
                    onBillItemClickListener.onBillItemClick(arrayListBill.get(position), position);
                }
            }
        });
        return convertView;
    }
    public interface OnBillItemClickListener {
        void onBillItemClick(Bill bill, int position);
    }
    private OnBillItemClickListener onBillItemClickListener;

    public void setOnBillItemClickListener(OnBillItemClickListener listener) {
        this.onBillItemClickListener = listener;
    }
    private int getResourceId(Context context, String resourceName) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }
    public List<Bill> getArrHistoryOption() {
        return arrayListBill;
    }
}
