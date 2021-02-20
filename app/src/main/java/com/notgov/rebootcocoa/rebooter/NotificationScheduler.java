package com.notgov.rebootcocoa.rebooter;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.notgov.rebootcocoa.models.NotificationMode;
import com.notgov.rebootcocoa.models.NotificationTime;
import com.notgov.rebootcocoa.models.Settings;
import java.util.Calendar;

public class NotificationScheduler {

  private static final String TAG = "Scheduler";

  private static final int DAILY_REBOOT_ALARM_REQUEST_CODE = 123;

  /**
   * Schedule a recurring alarm to notify users at the given time specified in
   * {@code hourOfDay}. This is safe to call at any time (even if an alarm has
   * already been created in the past.)
   */
  public static void rescheduleAlarm(Context context, Settings settings) {
    AlarmManager alarmManager =
        (AlarmManager) context.getSystemService(ALARM_SERVICE);

    PendingIntent alarmIntent = createDailyRebootAlarmIntent(context);

    // Ensure existing alarms are stopped before we start.
    alarmManager.cancel(alarmIntent);

    NotificationTime notificationTime =
        settings.getNotificationTime().getValue();
    if (notificationTime == null) {
      notificationTime = Settings.DEFAULT_TIME;
    }
    NotificationMode mode = settings.getNotificationMode().getValue();
    if (mode == null) {
      mode = Settings.DEFAULT_MODE;
    }

    Calendar initialAlarmTime = Calendar.getInstance();
    long interval;
    if (NotificationMode.BEAST.equals(mode)) {
      interval = 5000;
      // For BEAST mode, just set the initial alarm time to now.
    } else {
      interval = AlarmManager.INTERVAL_DAY;
      initialAlarmTime.set(Calendar.HOUR_OF_DAY, notificationTime.hour);
      initialAlarmTime.set(Calendar.MINUTE, notificationTime.minute);
      initialAlarmTime.set(Calendar.SECOND, 0);
      initialAlarmTime.set(Calendar.MILLISECOND, 0);
      if (initialAlarmTime.before(Calendar.getInstance())) {
        initialAlarmTime.add(Calendar.DATE, 1);
      }
    }
    Log.w(TAG, "Next alarm is: " + initialAlarmTime.getTime());
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        initialAlarmTime.getTimeInMillis(),
        interval,
        alarmIntent);
  }

  /**
   * Construct a {@link PendingIntent} for showing the COCOA reboot
   * notification. This intent can be used for starting and cancelling existing
   * alarms.
   */
  private static PendingIntent createDailyRebootAlarmIntent(Context context) {
    Intent alarmIntent = new Intent(context, DailyAlarmReceiver.class);
    return PendingIntent
        .getBroadcast(
            context,
            DAILY_REBOOT_ALARM_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
