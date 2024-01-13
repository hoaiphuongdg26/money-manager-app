package com.example.proj_moneymanager.activities.Profile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proj_moneymanager.R;

import java.util.List;

public class ProfileAdapter  extends BaseAdapter {
    private Activity myContext;
    List<Profile_Option> arrProfileOption;
    public ProfileAdapter(Activity context, List<Profile_Option> profile_optionList){
        this.myContext = context;
        arrProfileOption = profile_optionList;
    }
    @Override
    public int getCount() {
        return arrProfileOption.size();
    }

    @Override
    public Object getItem(int position) {
        return arrProfileOption.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Đổ từng item trong list vào từng dòng
        convertView = inflater.inflate(R.layout.lv_item_profile, null);
        if (convertView == null) {
            convertView = LayoutInflater.from(myContext).inflate(R.layout.lv_item_profile, parent, false);
        }
        //get item
        Profile_Option profileOption = (Profile_Option) getItem(position);
        //get view
        TextView txt_Label = (TextView) convertView.findViewById(R.id.textview_labelName_Profile);
        txt_Label.setText(arrProfileOption.get(position).getLabel());

        TextView txt_labelInfo = (TextView) convertView.findViewById(R.id.textview_Name_Profile);
        if(position==1){
            String hiddenPasswd = " ";
            //for(int i = 0;i<arrProfileOption.get(position).getLabelInfo().length();i++) hiddenPasswd= hiddenPasswd+"*";
            txt_labelInfo.setText(hiddenPasswd);
        }
        else txt_labelInfo.setText(arrProfileOption.get(position).getLabelInfo());

        ImageView img_iconOption = (ImageView) convertView.findViewById(R.id.imgv_personal);
        img_iconOption.setImageResource(arrProfileOption.get(position).getImageOption());

        return convertView;
    }
}