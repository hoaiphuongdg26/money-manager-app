package com.example.proj_moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<optProfile> {
    public ListAdapter(Context context, ArrayList<optProfile> optProfileArrayList) {
        super(context, R.layout.lv_item_profile, optProfileArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        optProfile opt = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_item_profile, parent, false);

        }

        ImageView imageView = convertView.findViewById(R.id.imgv_personal);
        TextView labelName = convertView.findViewById(R.id.textview_labelName_Profile);
        TextView Name = convertView.findViewById(R.id.textview_Name_Profile);

        imageView.setImageResource(opt.imageID);
        labelName.setText(opt.label);
        Name.setText(opt.labelInfo);

        return super.getView(position, convertView, parent);
    }
}
