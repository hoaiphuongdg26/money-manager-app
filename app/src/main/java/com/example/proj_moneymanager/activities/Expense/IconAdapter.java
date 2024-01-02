package com.example.proj_moneymanager.activities.Expense;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.proj_moneymanager.R;

public class IconAdapter extends BaseAdapter {
    private Context context;

    // Danh sách các màu
    private int[] iconDrawables = {
            R.drawable.ic_car,
            R.drawable.ic_card,
            R.drawable.ic_flight_takeoff,
            R.drawable.ic_food,
            R.drawable.ic_gift,
            R.drawable.ic_money,
            R.drawable.ic_power,
            R.drawable.ic_shopping_cart,
            R.drawable.ic_water,
            R.drawable.ic_wifi,
            // ... Thêm các icon khác
    };
    private int iconelectedPosition = -1;
    public IconAdapter(EditCategoryFragment context) {
        this.context = context.requireContext();
    }

    @Override
    public int getCount() {
        return iconDrawables.length;
    }

    @Override
    public Object getItem(int position) {
        return iconDrawables[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridViewItem = convertView;

        if (gridViewItem == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridViewItem = inflater.inflate(R.layout.gv_item_icon, null);
        }
        ImageButton btnIcon = gridViewItem.findViewById(R.id.imagebutton_itemicon);
        btnIcon.setImageResource(iconDrawables[position]);
        btnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi ImageButton được chọn
                setSelectedPosition(position);

            }
        });

        // Highlight selected button
        btnIcon.setSelected(iconelectedPosition == position);

        return gridViewItem;
    }
    public void setSelectedPosition(int position) {
        iconelectedPosition = position;
        notifyDataSetChanged(); // Refresh GridView to update selected item
        notifyColorSelectedListener(iconDrawables[position]); // Notify the listener with the selected color
    }

    // Define a listener interface to notify the selected color
    public interface OnIconSelectedListener {
        void onColorSelected(int colorDrawableId);
    }

    private IconAdapter.OnIconSelectedListener iconSelectedListener;

    public void setOnColorSelectedListener(IconAdapter.OnIconSelectedListener listener) {
        iconSelectedListener = listener;
    }

    private void notifyColorSelectedListener(int iconDrawableId) {
        if (iconSelectedListener != null) {
            iconSelectedListener.onColorSelected(iconDrawableId);
        }
    }
}
