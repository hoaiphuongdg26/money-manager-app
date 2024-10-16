package com.example.proj_moneymanager.activities.Expense;
// ColorAdapter.java

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.databinding.DialogEditCategoryBinding;

public class ColorAdapter extends BaseAdapter {
    private static Context context;
    public ColorAdapter(Context context) {
        this.context = context;
    }

    // Danh sách các màu
    private static int[] colorDrawables = {
            R.drawable.colorbutton_0,
            R.drawable.colorbutton_1,
            R.drawable.colorbutton_2,
            R.drawable.colorbutton_3,
            R.drawable.colorbutton_4,
            R.drawable.colorbutton_5,
            R.drawable.colorbutton_6,
            R.drawable.colorbutton_7,
            R.drawable.colorbutton_8,
            R.drawable.colorbutton_9,
            R.drawable.colorbutton_10,
            R.drawable.colorbutton_11,
            R.drawable.colorbutton_12,
            R.drawable.colorbutton_13,
            R.drawable.colorbutton_14,
            R.drawable.colorbutton_15
            // ... Thêm các màu khác
    };
    private int selectedPosition = -1;
    @Override
    public int getCount() {
        return colorDrawables.length;
    }
    @Override
    public Object getItem(int position) {
        return colorDrawables[position];
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridViewItem = convertView;

        if (gridViewItem == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridViewItem = inflater.inflate(R.layout.gv_item_color, null);
        }
        ImageButton btnColor = gridViewItem.findViewById(R.id.imagebutton_itemcolor);
        btnColor.setImageResource(colorDrawables[position]);
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi ImageButton được chọn
                setSelectedPosition(position);
                int colorDrawableId = colorDrawables[position];
                selectedColorResourceName = getResourceName(colorDrawableId);
//                Toast.makeText(context, "Resource Name: " + colorDescription, Toast.LENGTH_LONG).show();
            }
        });

        // Highlight selected button
        btnColor.setSelected(selectedPosition == position);

        return gridViewItem;
    }
    static String getResourceName(int colorDrawableId) {
        try {
            return context.getResources().getResourceEntryName(colorDrawableId);
        } catch (Resources.NotFoundException e) {
            // Log the exception or handle it accordingly
            e.printStackTrace();
            return null;
        }
    }
    public static int getPositionByResourceName(String resourceName) {
        for (int i = 0; i < colorDrawables.length; i++) {
            int iconDrawableId = colorDrawables[i];
            String currentResourceName = getResourceName(iconDrawableId);
            if (currentResourceName != null && currentResourceName.equals(resourceName)) {
                return i; // Trả về vị trí của resource tìm thấy
            }
        }
        return -1; // Nếu không tìm thấy resource, trả về -1
    }
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < colorDrawables.length) {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh GridView to update selected item
        }
    }
    private String selectedColorResourceName;
    public String getSelectedColorResourceName() {
        return selectedColorResourceName;
    }
    public void setSelectedColorResourceName(String selectedColorResourceName){
        this.selectedColorResourceName = selectedColorResourceName;
    }
}

