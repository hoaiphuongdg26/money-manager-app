package com.example.proj_moneymanager.activities.Expense;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.proj_moneymanager.R;

public class IconAdapter extends BaseAdapter {
    private static Context context;
    public IconAdapter(Context context) {
        this.context = context;
    }
    private OnIconClickListener iconClickListener;
    private OnIconSelectedListener iconSelectedListener;

    // Danh sách các màu 30dpx30dp
    private static int[] iconDrawables = {
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
            R.drawable.ic_game,
            // ... Thêm các icon khác
    };
    private int selectedPosition = -1;
    public IconAdapter(NewCategoryFragment context) {
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
    public IconAdapter(EditCategoryAdapter editCategoryAdapter) {
        context = editCategoryAdapter.getContext();
        this.iconClickListener = editCategoryAdapter;
    }

    public void setOnIconSelectedListenerDialog(OnIconSelectedListener onIconSelectedListener) {
        this.iconSelectedListener = onIconSelectedListener;
    }

    // Define a listener interface to notify the selected icon
    public interface OnIconSelectedListener {
        void onIconSelected(String iconDescription);
    }
    private void notifyIconSelectedListener(int iconDrawableId) {
        if (iconSelectedListener != null) {
            String iconDescription = getResourceName(iconDrawableId);
//                Toast.makeText(context, "Resource Name: " + iconDescription, Toast.LENGTH_LONG).show();
            // Notify the listener with the selected icon description
            if (iconClickListener != null) {
                iconSelectedListener.onIconSelected(iconDescription);
            }

        }
    }
    public interface OnIconClickListener {
        void onIconClick(String iconDescription);
    }
    public IconAdapter(NewCategoryFragment context, IconAdapter.OnIconClickListener iconClickListener) {
        this.context = context.requireContext();
        this.iconClickListener = iconClickListener;
    }
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
                int iconDrawableId = iconDrawables[position];
                String iconDescription = getResourceName(iconDrawableId);
//                Toast.makeText(context, "Resource Name: " + iconDescription, Toast.LENGTH_LONG).show();
                // Notify the listener with the selected icon description
                if (iconClickListener != null) {
                    iconClickListener.onIconClick(iconDescription);
                }
            }
        });

        // Highlight selected button
        btnIcon.setSelected(selectedPosition == position);

        return gridViewItem;
    }
    static String getResourceName(int iconDrawableId) {
        try {
            return context.getResources().getResourceEntryName(iconDrawableId);
        } catch (Resources.NotFoundException e) {
            // Log the exception or handle it accordingly
            e.printStackTrace();
            return null;
        }
    }
    public static int getPositionByResourceName(String resourceName) {
        for (int i = 0; i < iconDrawables.length; i++) {
            int iconDrawableId = iconDrawables[i];
            String currentResourceName = getResourceName(iconDrawableId);
            if (currentResourceName != null && currentResourceName.equals(resourceName)) {
                return i; // Trả về vị trí của resource tìm thấy
            }
        }
        return -1; // Nếu không tìm thấy resource, trả về -1
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < iconDrawables.length) {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh GridView to update selected item
            notifyIconSelectedListener(iconDrawables[position]);
        }// Notify the listener with the selected icon
    }


}
