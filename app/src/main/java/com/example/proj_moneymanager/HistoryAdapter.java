package com.example.proj_moneymanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter  extends BaseAdapter {
    private Activity myContext;
    List<ProfileOption> arrHistoryOption;
    public HistoryAdapter(Activity context, List<ProfileOption> history_optionList){
        this.myContext = context;
        arrHistoryOption = history_optionList;
    }
    @Override
    public int getCount() {
        return arrHistoryOption.size();
    }

    @Override
    public Object getItem(int position) {
        return arrHistoryOption.get(position);
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
            convertView = LayoutInflater.from(myContext).inflate(R.layout.lv_item_profile, parent, false);
        }
        //get item
        ProfileOption historyOption = (ProfileOption) getItem(position);
        //get view
        TextView txt_Label = (TextView) convertView.findViewById(R.id.textview_labelName_Profile);
        txt_Label.setText(arrHistoryOption.get(position).getLabel());

        TextView txt_labelInfo = (TextView) convertView.findViewById(R.id.textview_Name_Profile);
        txt_labelInfo.setText(arrHistoryOption.get(position).getLabelInfo());

        ImageView img_iconOption = (ImageView) convertView.findViewById(R.id.imgv_personal);
        img_iconOption.setImageResource(arrHistoryOption.get(position).getImageOption());

        return convertView;
    }
}
