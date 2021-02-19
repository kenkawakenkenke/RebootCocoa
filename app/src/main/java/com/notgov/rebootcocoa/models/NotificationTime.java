package com.notgov.rebootcocoa.models;

import androidx.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data model representing the notification time, the time of day at which
 * notifications should be scheduled.
 */
public class NotificationTime {

  private static final Pattern TIME_PATTERN = Pattern.compile(
      "([0-9]+):([0-9]+)");

  /**
   * The hour of day to schedule the notification.
   */
  public final int hour;

  /**
   * The minute of the hour to schedule the notification.
   */
  public final int minute;

  private NotificationTime(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
  }

  /**
   * Generator for {@link NotificationTime}. Returns null if the provided
   * hours/minutes are invalid.
   */
  @Nullable
  public static NotificationTime fromHourMinute(int hour, int minute) {
    if (hour < 0 || hour >= 24) {
      return null;
    }
    if (minute < 0 || minute >= 60) {
      return null;
    }
    return new NotificationTime(hour, minute);
  }

  /**
   * Equivalent of {@link #fromHourMinute} but throwing NPE if the input is
   * invalid. Useful for constants that you're confident are valid.
   */
  public static NotificationTime fromHourMinuteOrDie(int hour, int minute) {
    NotificationTime time = fromHourMinute(hour, minute);
    if (time == null) {
      throw new NullPointerException("hour-minute is invalid: "
          + hour
          + " "
          + minute);
    }
    return time;
  }

  /**
   * Generator for {@link NotificationTime} based on a text representation of
   * time (e.g "12:34"). Returns {@code null} for invalid text.
   */
  @Nullable
  public static NotificationTime fromText(String timeStr) {
    Matcher matcher = TIME_PATTERN.matcher(timeStr);
    if (!matcher.matches()) {
      return null;
    }
    int hour = Integer.parseInt(matcher.group(1));
    int minutes = Integer.parseInt(matcher.group(2));
    return fromHourMinute(hour, minutes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hour, minute);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof NotificationTime)) {
      return false;
    }
    NotificationTime otherTime = (NotificationTime) other;
    return hour == otherTime.hour && minute == otherTime.minute;
  }

  /**
   * Formats to a text representation of the time (e.g., "12:34").
   */
  @Override
  public String toString() {
    return String.format(Locale.US, "%02d:%02d", hour, minute);
  }
}
