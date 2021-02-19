package com.notgov.rebootcocoa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

/**
 * App main activity, sets up the daily alarms.
 */
public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";

  private static final int DAILY_REBOOT_ALARM_REQUEST_CODE = 123;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    scheduleAlarm(this, 8);
  }

  /**
   * Schedule a recurring alarm to notify users at the given time specified in
   * {@code hourOfDay}. This is safe to call at any time (even if an alarm
   * has already been created in the past.)
   */
  private static void scheduleAlarm(Context context, int hourOfDay) {
    AlarmManager alarmManager =
        (AlarmManager) context.getSystemService(ALARM_SERVICE);

    PendingIntent alarmIntent = createDailyRebootAlarmIntent(context);

    // Ensure existing alarms are stopped before we start.
    alarmManager.cancel(alarmIntent);

    // Let's just arbitrarily notify at a fixed hour everyday.
    Calendar initialAlarmTime = Calendar.getInstance();
    initialAlarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
    initialAlarmTime.set(Calendar.MINUTE, 0);
    initialAlarmTime.set(Calendar.SECOND, 0);
    initialAlarmTime.set(Calendar.MILLISECOND, 0);
    if (initialAlarmTime.before(Calendar.getInstance())) {
      initialAlarmTime.add(Calendar.DATE, 1);
    }
    Log.w(TAG, "Next alarm is: "+initialAlarmTime.getTime());
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        initialAlarmTime.getTimeInMillis(),
        AlarmManager.INTERVAL_DAY,
        alarmIntent);
  }

  /**
   * Construct a {@link PendingIntent} for showing the COCOA reboot
   * notification. This intent can be used for starting and cancelling
   * existing alarms.
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