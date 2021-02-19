package com.notgov.rebootcocoa.models;

/**
 * Enum describing various operation modes of the notification scheduler.
 */
public enum NotificationMode {
  /**
   * The "normal" operation, which shows a notification every day.
   */
  NORMAL,
  /**
   * The "beast-mode", which shows a notification frequently (typically every
   * minute or so. This is meant only for debugging.
   */
  BEAST;

  /**
   * Returns a {@link NotificationMode} for the given ordinal, but falling back
   * to {@link NotificationMode#NORMAL} for invalid values.
   */
  public static NotificationMode fromOrdinalSafe(int ordinal) {
    if (ordinal < 0 || ordinal >= NotificationMode.values().length) {
      return NORMAL;
    }
    return NotificationMode.values()[ordinal];
  }
}
