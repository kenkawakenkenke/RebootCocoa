package com.notgov.rebootcocoa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.notgov.rebootcocoa.databinding.ActivityMainBinding;
import com.notgov.rebootcocoa.models.NotificationMode;
import com.notgov.rebootcocoa.models.NotificationTime;
import com.notgov.rebootcocoa.models.Settings;
import com.notgov.rebootcocoa.models.StoredSettings;
import com.notgov.rebootcocoa.viewmodels.SettingsViewModel;
import java.util.Calendar;

/**
 * App main activity, sets up the daily alarms.
 */
public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  private static final int DAILY_REBOOT_ALARM_REQUEST_CODE = 123;

  private StoredSettings storedSettings;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    storedSettings =
        new StoredSettings(this.getPreferences(Context.MODE_PRIVATE));

    // Setup our view and bind to the input viewmodel.
    ActivityMainBinding binding = DataBindingUtil.setContentView(this,
        R.layout.activity_main);
    SettingsViewModel viewModel =
        new ViewModelProvider(this).get(SettingsViewModel.class);
    viewModel.initialize(storedSettings);
    binding.setViewModel(viewModel);
    binding.setLifecycleOwner(this);

    // Setup some extra bindings that xml bindings don't support.
    String notificationTimeFormatErrorMessage =
        getString(R.string.main_settings_time_error_wrongformat);
    // When notification time is invalid, show an error message on the input
    // text box.
    viewModel.viewBoundSettings.getNotificationTime().observe(this,
        time ->
            binding.textTimeNotify.setError(time != null ? null :
                notificationTimeFormatErrorMessage)
    );

    // Reschedule our alarm when the stored settings have changed.
    // Unfortunately, this way of registering on individual fields mean that
    // on save, changes will be observed on both of these fields so we end up
    // registering twice. This shouldn't be too much of a problem, but is ugly.
    storedSettings.getNotificationTime().observe(this,
        v -> scheduleAlarm());
    storedSettings.getNotificationMode().observe(this,
        v -> scheduleAlarm());

    scheduleAlarm();
  }

  @Override
  protected void onResume() {
    super.onResume();
    storedSettings.resume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    storedSettings.pause();
  }

  /**
   * Schedule a recurring alarm to notify users at the given time specified in
   * {@code hourOfDay}. This is safe to call at any time (even if an alarm has
   * already been created in the past.)
   */
  private void scheduleAlarm() {
    AlarmManager alarmManager =
        (AlarmManager) getSystemService(ALARM_SERVICE);

    PendingIntent alarmIntent = createDailyRebootAlarmIntent(this);

    // Ensure existing alarms are stopped before we start.
    alarmManager.cancel(alarmIntent);

    NotificationTime notificationTime =
        storedSettings.getNotificationTime().getValue();
    if (notificationTime == null) {
      notificationTime = Settings.DEFAULT_TIME;
    }
    NotificationMode mode = storedSettings.getNotificationMode().getValue();
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