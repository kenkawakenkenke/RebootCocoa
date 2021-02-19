package com.notgov.rebootcocoa.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.notgov.rebootcocoa.models.StoredSettings;
import com.notgov.rebootcocoa.models.ViewBoundSettings;

/**
 * View model representing the user input form for {@link
 * com.notgov.rebootcocoa.models.Settings}.
 */
public class SettingsViewModel extends ViewModel {

  /**
   * The actual {@link com.notgov.rebootcocoa.models.Settings} model current
   * bound to view, representing the current user input for settings.
   */
  public final ViewBoundSettings viewBoundSettings = new ViewBoundSettings();

  private StoredSettings storedSettings;
  private LiveData<Boolean> sameAsStored;

  public void initialize(StoredSettings storedSettings) {
    this.storedSettings = storedSettings;

    // Initialize presented settings from the stored data.
    viewBoundSettings.setFrom(storedSettings);

    sameAsStored = viewBoundSettings.isEqual(storedSettings);
  }

  /**
   * Returns true if the current user input settings are the same as what's
   * stored. If this is true, there's no need to save.
   */
  public LiveData<Boolean> getSameAsStored() {
    return sameAsStored;
  }

  /**
   * Returns true if the current user input is valid, so is ok to save.
   */
  public LiveData<Boolean> inputValid = viewBoundSettings.isValid();

  /**
   * Callback for saving the current user input settings.
   */
  public void onSave() {
    storedSettings.setFrom(viewBoundSettings);
    viewBoundSettings.setFrom(storedSettings);
  }

  /**
   * Callback for cancelling (and resetting) the current user input settings.
   */
  public void onCancel() {
    viewBoundSettings.setFrom(storedSettings);
  }
}
