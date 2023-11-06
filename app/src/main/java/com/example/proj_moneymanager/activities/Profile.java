package com.example.proj_moneymanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj_moneymanager.ListAdapter;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.optProfile;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        int[] imageID = {R.drawable.icon_person, R.drawable.icon_lock, R.drawable.icon_switch, R.drawable.notification};
        String[] label = {"Name", "Password", "Switch Account", "Notification"};
        String[] labelInfo = {"Group03", "******", "group03@gmail.com", ""};

        ArrayList<optProfile> optProfileArrayList = new ArrayList<>();

        for (int i = 0; i < imageID.length; i++) {
            optProfile opt = new optProfile(label[i], labelInfo[i], imageID[i]);
            optProfileArrayList.add(opt);
        }

        ListAdapter listAdapter = new ListAdapter(this, optProfileArrayList);
        ListView listView = findViewById(R.id.lv_optProfile);
        listView.setAdapter(listAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Xử lý sự kiện khi một mục trên ListView được nhấn
            }
        });
    }
}
