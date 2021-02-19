package com.notgov.rebootcocoa.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import java.util.Objects;

/**
 * Abstract data class representing the user settings for the app.
 * <ul>
 *   <li>Notification time: the time to show the notification each day</li>
 *   <li>Notification mode: the notification interval setting</li>
 * </ul>
 */
public abstract class Settings {

  /**
   * Default value for the notification time.
   */
  public static final NotificationTime DEFAULT_TIME =
      NotificationTime.fromHourMinuteOrDie(8, 0);

  /**
   * Default value for the notification mode.
   */
  public static final NotificationMode DEFAULT_MODE = NotificationMode.NORMAL;


  /**
   * The time of day to notify every day (in hours and minutes). Only meaningful
   * if {@code notificationMode} is {@link NotificationMode#NORMAL}.
   */
  public abstract LiveData<NotificationTime> getNotificationTime();

  /**
   * Operation modes of the notification scheduler.
   */
  public abstract LiveData<NotificationMode> getNotificationMode();

  /**
   * Copy values from the given {@code settings}. NB, this is a one-time copy,
   * meaning that calling this method *won't* set us up to be "observers" of the
   * given settings in any way.
   *
   * @throws IllegalArgumentException if the given {@code settings} is not
   * {@link Settings#isCurrentlyValid}.
   */
  public final void setFrom(Settings settings) {
    if (!settings.isCurrentlyValid()) {
      throw new IllegalArgumentException("Tried to save invalid settings: "
          + settings);
    }
    NotificationTime time = settings.getNotificationTime().getValue();
    NotificationMode mode = settings.getNotificationMode().getValue();
    if (time == null || mode == null) {
      // We've already checked above, so this shouldn't happen. But just to make
      // nullability checker happy.
      return;
    }
    setFromFields(time, mode);
  }

  /**
   * Set the given field values to this {@link Settings}.
   */
  public abstract void setFromFields(NotificationTime time,
                                     NotificationMode mode);

  /**
   * Returns true if the current snapshot values of ourselves and the provided
   * {@link Settings} are equal.
   */
  private boolean isCurrentlyEqual(Settings other) {
    if (!Objects.equals(getNotificationTime().getValue(),
        other.getNotificationTime().getValue())) {
      return false;
    }
    if (!Objects.equals(getNotificationMode().getValue(),
        other.getNotificationMode().getValue())) {
      return false;
    }
    return true;
  }

  /**
   * Returns a {@link LiveData} describing whether we and the provided {@link
   * Settings} are equal.
   */
  public LiveData<Boolean> isEqual(Settings other) {
    MediatorLiveData<Boolean> diff = new MediatorLiveData<>();
    Runnable differ = () -> diff.setValue(this.isCurrentlyEqual(other));
    diff.addSource(getNotificationTime(), v -> differ.run());
    diff.addSource(getNotificationMode(), v -> differ.run());
    diff.addSource(other.getNotificationTime(), v -> differ.run());
    diff.addSource(other.getNotificationMode(), v -> differ.run());
    return diff;
  }
//
//  /**
//   * Validates the value of a time object.
//   */
//  static boolean validate(@Nullable Pair<Integer, Integer> notificationTime) {
//    if (notificationTime == null) {
//      return false;
//    }
//    int hours = notificationTime.first;
//    if (hours < 0 || hours >= 24) {
//      return false;
//    }
//    int minutes = notificationTime.second;
//    if (minutes < 0 || minutes >= 60) {
//      return false;
//    }
//    return true;
//  }
//
//  /**
//   * Validates the value of a {@link NotificationMode}.
//   */
//  static boolean validate(@Nullable NotificationMode mode) {
//    return mode != null;
//  }

  /**
   * Returns true if the current value of ourselves is valid (i.e is ok to be
   * saved, and used for operation.)
   */
  boolean isCurrentlyValid() {
    return this.getNotificationTime() != null
        && this.getNotificationMode() != null;
  }

  /**
   * Returns a {@link LiveData} describing whether we are valid.
   */
  public LiveData<Boolean> isValid() {
    MediatorLiveData<Boolean> isValid = new MediatorLiveData<>();
    Runnable updater = () -> isValid.setValue(isCurrentlyValid());
    isValid.addSource(getNotificationTime(), v -> updater.run());
    isValid.addSource(getNotificationMode(), v -> updater.run());
    return isValid;
  }
}
