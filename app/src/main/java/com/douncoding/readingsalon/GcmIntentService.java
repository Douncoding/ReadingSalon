package com.douncoding.readingsalon;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {super("GcmIntentService");}

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Received: " + extras.toString());
                String key1 = extras.getString("key1");
                String key2 = extras.getString("key2");

                if (key1 == null || key2 == null) {
                    Log.w(TAG, "푸쉬 수신: 잘못된 매개변수 수신: ");
                    return;
                } else {
                    sendNotification(key1, Integer.valueOf(key2));
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String message, int contentId) {
        NotificationManager manager
                = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("contents", contentId);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setAutoCancel(true);
        builder.setContentIntent(contentIntent);

        manager.notify(Constants.NOTIFICATION_GCM_ID, builder.build());
    }
}
