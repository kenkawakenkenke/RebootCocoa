package com.notgov.rebootcocoa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;

/**
 * Handler for the daily alarms. Shows a notification for the user, which opens
 * the COCOA app.
 */
public class DailyAlarmReceiver extends BroadcastReceiver {
private static final String TAG = "DailyAlarmReceiver";

  // Channel ID used for the "reboot" notification.
  public static final String REBOOT_COCOA_NOTIFICATION_CHANNEL_ID
      = "com.notgov.rebootcocoa.rebootnotif";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Received alarm");

    // Setup the notification channel.
    maybeSetupNotificationChannel(context);

    // Intent to restart COCOA app.
    PendingIntent rebootCocoaIntent =
        PendingIntent.getActivity(
            context,
            0,
            context.getPackageManager().getLaunchIntentForPackage(
                "jp.go.mhlw.covid19radar"),
            PendingIntent.FLAG_ONE_SHOT);

    Notification notification = new NotificationCompat.Builder(context,
        REBOOT_COCOA_NOTIFICATION_CHANNEL_ID)
        .setOngoing(true)
        .setSmallIcon(R.drawable.baseline_autorenew_24)
        .setContentTitle(context.getString(R.string.reboot_notification_title))
        .setContentText(context.getString(R.string.reboot_notification_content))
        .setPriority(NotificationManager.IMPORTANCE_HIGH)
        .setCategory(Notification.CATEGORY_SERVICE)
        .setAutoCancel(true)
        .setOngoing(false)
        .setContentIntent(rebootCocoaIntent)
        .build();

    NotificationManager notificationManager =
        (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(0, notification);
  }

  /**
   * Sets up the notification channel used for rebooting COCOA.
   */
  private static void maybeSetupNotificationChannel(Context context) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
      // Notification channels didn't exist before O.
      return;
    }
    NotificationManager notificationManager =
        (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationChannel channel = notificationManager.getNotificationChannel(
        DailyAlarmReceiver.REBOOT_COCOA_NOTIFICATION_CHANNEL_ID);
    if (channel != null) {
      // We're good to go!
      return;
    }
    notificationManager.createNotificationChannel(
        new NotificationChannel(
            DailyAlarmReceiver.REBOOT_COCOA_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.reboot_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH));
  }
}