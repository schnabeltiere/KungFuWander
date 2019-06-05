package kungfuwander.main;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service {

    public static final String CHANNEL_ID = "myService";
    public static final String CHANNEL_NAME = "Walker Service";
    public static final String CONTENT_TITLE = "Hello walking person";
    public static final int CURRENT_HIKING_ID = 69;

    private NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            channelId = "";
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(input)
                .setSmallIcon(R.drawable.kfwl2)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(CURRENT_HIKING_ID, notification);

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }

    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_NONE);

        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        // consider NotificationManagerCompat?
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelId;
    }

    @Deprecated
    // use this in future, but manager is always null
    // even if i init it in the method
    public void updateNotification(NotificationManager manager, String input){
        // consider NotificationManagerCompat?
        // remove this parameter

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(input)
                .setSmallIcon(R.drawable.kfwl2)
                .build();

        manager.notify(CURRENT_HIKING_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
