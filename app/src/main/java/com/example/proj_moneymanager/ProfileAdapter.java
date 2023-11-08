package com.example.proj_moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProfileAdapter  extends BaseAdapter {
    Context myContext;
    int myLayout;
    List<Profile_Option> arrProfileOption;
    public ProfileAdapter(Context context, int layout, List<Profile_Option > profile_optionList){
            myContext = context;
            myLayout = layout;
            arrProfileOption = profile_optionList;
    }
    @Override
    public int getCount() {
        return arrProfileOption.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(myLayout, null);
        if (convertView == null) {
            convertView = LayoutInflater.from(myContext).inflate(R.layout.lv_item_profile, parent, false);
        }
        //get item
        Profile_Option profileOption = (Profile_Option) getItem(position);
        //get view
        TextView txt_Label = (TextView) convertView.findViewById(R.id.textview_labelName_Profile);
        txt_Label.setText(arrProfileOption.get(position).label);

        TextView txt_labelInfo = (TextView) convertView.findViewById(R.id.textview_Name_Profile);
        txt_labelInfo.setText(arrProfileOption.get(position).labelInfo);

        ImageView img_iconOption = (ImageView) convertView.findViewById(R.id.imgv_personal);
        img_iconOption.setImageResource(arrProfileOption.get(position).imageOption);

        return convertView;
    }
}
