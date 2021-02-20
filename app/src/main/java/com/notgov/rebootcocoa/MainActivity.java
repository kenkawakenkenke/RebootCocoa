package com.notgov.rebootcocoa;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.notgov.rebootcocoa.databinding.ActivityMainBinding;
import com.notgov.rebootcocoa.models.StoredSettings;
import com.notgov.rebootcocoa.rebooter.NotificationScheduler;
import com.notgov.rebootcocoa.viewmodels.SettingsViewModel;

/**
 * App main activity, sets up the daily alarms.
 */
public class MainActivity extends AppCompatActivity {

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
        new ViewModelProvider(this,
            new SettingsViewModel.Factory(storedSettings))
            .get(SettingsViewModel.class);
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
        v -> NotificationScheduler.rescheduleAlarm(this, storedSettings));
    storedSettings.getNotificationMode().observe(this,
        v -> NotificationScheduler.rescheduleAlarm(this, storedSettings));

    NotificationScheduler.rescheduleAlarm(this, storedSettings);
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
}