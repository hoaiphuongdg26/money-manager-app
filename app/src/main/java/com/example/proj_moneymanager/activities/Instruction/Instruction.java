package com.example.proj_moneymanager.activities.Instruction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.proj_moneymanager.MainActivity;
import com.example.proj_moneymanager.R;
import com.example.proj_moneymanager.activities.Login;

public class Instruction  extends AppCompatActivity {
    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;
    Button backbtn, nextbtn, skipbtn;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    private boolean isFirstRun;
    private boolean isReopening;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kiểm tra xem đã xem   hướng dẫn chưa
        SharedPreferences preferences = getSharedPreferences("MyPreferencesInstruction", MODE_PRIVATE);
        boolean hasViewedInstruction = preferences.getBoolean("hasViewedInstruction", false);
        if (hasViewedInstruction) {
            // Nếu đã xem hướng dẫn, chuyển đến màn hình đăng nhập
            Intent loginIntent = new Intent(Instruction.this, Login.class);
            startActivity(loginIntent);
            finish(); // Đóng Instruction để người dùng không thể quay lại nó bằng nút "Back"
        } else {
            setContentView(R.layout.instruction);

            backbtn = findViewById(R.id.backbtn);
            nextbtn = findViewById(R.id.nextbtn);
            skipbtn = findViewById(R.id.skipButton);

            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getitem(0) > 0) {

                        mSLideViewPager.setCurrentItem(getitem(-1), true);

                    }

                }
            });

            nextbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getitem(0) < 3)
                        mSLideViewPager.setCurrentItem(getitem(1), true);
                    else {

                        Intent i = new Intent(Instruction.this, Login.class);
                        startActivity(i);
                        finish();

                    }

                }
            });

            skipbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent i = new Intent(Instruction.this, Login.class);
                    startActivity(i);
                    finish();

                }
            });

            mSLideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
            mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

            viewPagerAdapter = new ViewPagerAdapter(this);

            mSLideViewPager.setAdapter(viewPagerAdapter);

            setUpindicator(0);
            mSLideViewPager.addOnPageChangeListener(viewListener);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.putBoolean("isReopening", false);
            editor.apply();
        }

    }

    public void setUpindicator(int position){

        dots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0 ; i < dots.length ; i++){

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.inactive,getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);

        }

        dots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setUpindicator(position);

            if (position > 0){

                backbtn.setVisibility(View.VISIBLE);

            }else {

                backbtn.setVisibility(View.INVISIBLE);

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getitem(int i){

        return mSLideViewPager.getCurrentItem() + i;
    }
}
