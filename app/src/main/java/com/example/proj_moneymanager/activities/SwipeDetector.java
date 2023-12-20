package com.example.proj_moneymanager.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintSet;

import com.example.proj_moneymanager.activities.MainActivity;

public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
    int SWIPE_THRESHOLD = 100;
    int SWIPE_VELOCITY_THRESHOLD = 100;
    public GestureDetector gestureDetector;
    private Context currentActivity;
    private Class<?> targetActivity;
    public SwipeDetector(Context currentActivity, Class<?> targetActivity) {
        this.currentActivity = currentActivity;
        this.targetActivity = targetActivity;
        gestureDetector = new GestureDetector(currentActivity, this); // Khởi tạo gestureDetector ở đây
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();

        if (Math.abs(distanceX) > Math.abs(distanceY) &&
                Math.abs(distanceX) > SWIPE_THRESHOLD &&
                Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0) {
                // Vuốt sang phải
                Intent intent = new Intent(currentActivity, targetActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                currentActivity.startActivity(intent);
                //Toast.makeText(currentActivity, "Swiped Right", Toast.LENGTH_SHORT).show();
            }
//            else {
//                // Vuốt sang trái
//            }
            return true;
        }
        return false;
    }
    public void onTouchEvent(MotionEvent motionEvent) {}
}

