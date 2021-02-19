package com.notgov.rebootcocoa.models;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * An implementation of {@link Settings} bound to the {@link SharedPreferences}
 * storage. Any changes to the stored values will be "output" in the respective
 * {@link Settings}-defined getter fields.
 */
public class StoredSettings extends Settings
    implements OnSharedPreferenceChangeListener {

  private static final String TAG = "StoredUserSettings";

  /**
   * Shared preference keys for each setting field.
   */
  private static final String PREF_NOTIFICATION_HOUR = "NOTIFICATION_HOUR";
  private static final String PREF_NOTIFICATION_MIN = "NOTIFICATION_MIN";
  private static final String PREF_NOTIFICATION_MODE = "NOTIFICATION_MODE";

  private final SharedPreferences preferences;

  private final MutableLiveData<NotificationTime> notificationTime =
      new MutableLiveData<>();
  private final MutableLiveData<NotificationMode> notificationMode =
      new MutableLiveData<>();

  public StoredSettings(SharedPreferences preferences) {
    this.preferences = preferences;

    updateFromLatestPreferences();
  }

  /**
   * Signs us up to receive updates from the {@link SharedPreferences} meant to
   * be called in the onResume activity lifecycle stage.
   */
  public void resume() {
    preferences.registerOnSharedPreferenceChangeListener(this);
  }

  /**
   * Stops us from receiving further updates from the {@link SharedPreferences}
   * meant to be called in the onPause activity lifecycle stage.
   */
  public void pause() {
    preferences.unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public LiveData<NotificationTime> getNotificationTime() {
    return notificationTime;
  }

  @Override
  public LiveData<NotificationMode> getNotificationMode() {
    return notificationMode;
  }

  @Override
  public void setFromFields(NotificationTime time,
                            NotificationMode mode) {
    Editor editor = preferences.edit();
    editor.putInt(PREF_NOTIFICATION_HOUR, time.hour);
    editor.putInt(PREF_NOTIFICATION_MIN, time.minute);
    editor.putInt(PREF_NOTIFICATION_MODE, mode.ordinal());
    editor.apply();
  }

  /**
   * Try to update our current state from what's currently stored in
   * preferences.
   */
  private void updateFromLatestPreferences() {
    int hours = preferences.getInt(PREF_NOTIFICATION_HOUR, DEFAULT_TIME.hour);
    int minutes = preferences.getInt(PREF_NOTIFICATION_MIN,
        DEFAULT_TIME.minute);
    int modeOrdinal = preferences.getInt(PREF_NOTIFICATION_MODE,
        DEFAULT_MODE.ordinal());

    NotificationTime time = NotificationTime.fromHourMinute(hours, minutes);
    NotificationMode mode = NotificationMode.fromOrdinalSafe(modeOrdinal);

    if (time == null) {
      Log.e(TAG,
          "preferences data is corrupt: " + time + " " + mode + ". Falling "
              + "back to default.");
      time = DEFAULT_TIME;
      mode = DEFAULT_MODE;
      // Try to overwrite the corrupt data.
      setFromFields(time, mode);
    }

    notificationTime.setValue(time);
    notificationMode.setValue(mode);
  }

  // Overridden from OnSharedPreferenceChangeListener.
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                        String s) {
    switch (s) {
      case PREF_NOTIFICATION_HOUR:
      case PREF_NOTIFICATION_MIN:
      case PREF_NOTIFICATION_MODE:
        updateFromLatestPreferences();
      default:
        // Nothing to do.
    }

  }
}

