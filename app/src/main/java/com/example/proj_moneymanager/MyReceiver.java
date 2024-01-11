package com.example.proj_moneymanager;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.proj_moneymanager.activities.Login;

import java.util.List;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (shouldShowNotification(context) && !isAppRunningInForeground(context)) {
            showNotification(context, "I Miss You", "\n" + "It has been more than 24 hours since you entered your personal income or expenses. Do not forget me!");
        }

    }
    private boolean shouldShowNotification(Context context) {
        long lastAccessTime = getSavedLastAccessTime(context);
        // Kiểm tra ứng dụng có đang chạy hay không
        boolean isAppRunning = isAppRunningInForeground(context);
        long currentTime = System.currentTimeMillis();
//        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000; // 24 giờ
        long twentyFourHoursInMillis = 60 * 1000; // 24 giờ

        return (currentTime - lastAccessTime) > twentyFourHoursInMillis;
    }

    // Phương thức hiển thị thông báo
    private void showNotification(Context context, String title, String message) {
        // Tạo intent để mở ứng dụng khi người dùng nhấn vào thông báo
        Intent resultIntent = new Intent(context, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Để đảm bảo thông báo biến mất khi được nhấn

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
        // Reset thời điểm truy cập cuối cùng sau khi hiển thị thông báo
        saveLastAccessTime(context, System.currentTimeMillis());
    }

    // Lưu trữ thời điểm truy cập cuối cùng bằng SharedPreferences
    public static void saveLastAccessTime(Context context, long time) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("lastAccessTime", time);
        editor.apply();
    }

    // Lấy thời điểm truy cập cuối cùng từ SharedPreferences
    private long getSavedLastAccessTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return preferences.getLong("lastAccessTime", 0);
    }
    private boolean isAppRunningInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
        if (processInfoList != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
                if (processInfo.processName.equals(context.getPackageName()) && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

}
