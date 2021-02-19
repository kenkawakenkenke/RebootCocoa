package com.notgov.rebootcocoa.models;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.notgov.rebootcocoa.R;

/**
 * This is an implementation of {@link Settings} meant for being directly bound
 * to the view. Given user inputs in {@link #notificationTimeHour} and {@link
 * #radioGroupNotificationModeCheckedButton}, this class will "output" clean
 * values (or null) in {@link Settings#getNotificationTime()} and {@link
 * Settings#getNotificationMode()}} respectively.
 */
public class ViewBoundSettings extends Settings {

  /**
   * The view-bound model of the notification time settings. This represents a
   * textual representation of the notification time (e.g "12:34").
   */
  public final MutableLiveData<String> notificationTimeText =
      new MutableLiveData<>();

  /**
   * The view-bound model of the notification mode. This represents the view ID
   * of the selected radio button for the notification mode.
   */
  public final MutableLiveData<Integer> radioGroupNotificationModeCheckedButton
      = new MediatorLiveData<>();

  /**
   * The "output" value representing the validated notification time. This is
   * either the pair of hour-minute, or null if the current input is invalid.
   */
  private final LiveData<NotificationTime> notificationTimeHour =
      Transformations.map(notificationTimeText, NotificationTime::fromText);

  /**
   * The "output" value representing the validated notification mode. This is
   * either the {@link NotificationMode}, or null if the current input is
   * invalid.
   */
  private final LiveData<NotificationMode> notificationMode =
      Transformations.map(radioGroupNotificationModeCheckedButton,
          ViewBoundSettings::modeFromRadioButtonViewId);

  @Override
  public LiveData<NotificationTime> getNotificationTime() {
    return notificationTimeHour;
  }

  @Override
  public LiveData<NotificationMode> getNotificationMode() {
    return notificationMode;
  }

  @Override
  public void setFromFields(NotificationTime time, NotificationMode mode) {
    notificationTimeText.setValue(time.toString());
    radioGroupNotificationModeCheckedButton.setValue(
        radioButtonIdFromMode(mode));
  }

  /**
   * Returns a {@link NotificationMode} represented by the radio button with the
   * given ID.
   */
  private static NotificationMode modeFromRadioButtonViewId(int radioButtonViewId) {
    if (radioButtonViewId == R.id.radioModeBeast) {
      return NotificationMode.BEAST;
    }
    return NotificationMode.NORMAL;
  }

  /**
   * Returns the view ID for the radio button mapped to the {@link
   * NotificationMode}.
   */
  private static int radioButtonIdFromMode(@Nullable NotificationMode mode) {
    if (mode == null) {
      return R.id.radioModeNormal;
    }
    switch (mode) {
      case BEAST:
        return R.id.radioModeBeast;
      case NORMAL:
      default:
        return R.id.radioModeNormal;
    }
  }
}
