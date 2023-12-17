package com.example.proj_moneymanager.activities.Setting;

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

public class SettingAdapter  extends BaseAdapter {
    private Activity myContext;
    List<Setting_Option> arrSettingOption;
    public SettingAdapter(Activity context, List<Setting_Option> Setting_OptionList){
        this.myContext = context;
        arrSettingOption = Setting_OptionList;
    }
    @Override
    public int getCount() {
        return arrSettingOption.size();
    }

    @Override
    public Object getItem(int position) {
        return arrSettingOption.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Đổ từng item trong list vào từng dòng
        convertView = inflater.inflate(R.layout.lv_item_setting, null);
        if (convertView == null) {
            convertView = LayoutInflater.from(myContext).inflate(R.layout.lv_item_setting, parent, false);
        }
        //get item
        Setting_Option SettingOption = (Setting_Option) getItem(position);
        //get view
        TextView txt_Label = (TextView) convertView.findViewById(R.id.textview_Language_Setting);
        txt_Label.setText(arrSettingOption.get(position).getLabel());

        ImageView img_iconOption = (ImageView) convertView.findViewById(R.id.imgv_language);
        img_iconOption.setImageResource(arrSettingOption.get(position).getImageOption());

        return convertView;
    }
}
