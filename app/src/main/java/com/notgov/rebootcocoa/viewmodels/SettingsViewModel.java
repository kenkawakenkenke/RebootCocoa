package com.notgov.rebootcocoa.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.notgov.rebootcocoa.models.StoredSettings;
import com.notgov.rebootcocoa.models.ViewBoundSettings;

/**
 * View model representing the user input form for {@link
 * com.notgov.rebootcocoa.models.Settings}.
 */
public class SettingsViewModel extends ViewModel {

  /**
   * ViewModel factory for constructing ourselves.
   */
  public static class Factory implements ViewModelProvider.Factory {

    private final StoredSettings storedSettings;

    public Factory(StoredSettings storedSettings) {
      this.storedSettings = storedSettings;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      if (!modelClass.equals(SettingsViewModel.class)) {
        throw new IllegalArgumentException("We only handle SettingsViewModel");
      }
      return (T) new SettingsViewModel(this.storedSettings);
    }
  }

  private final StoredSettings storedSettings;

  public SettingsViewModel(StoredSettings storedSettings) {
    this.storedSettings = storedSettings;

    // Initialize presented settings from the stored data.
    viewBoundSettings.setFrom(storedSettings);

    this.sameAsStored = viewBoundSettings.isEqual(storedSettings);
  }

  /**
   * The actual {@link com.notgov.rebootcocoa.models.Settings} model current
   * bound to view, representing the current user input for settings.
   */
  public final ViewBoundSettings viewBoundSettings = new ViewBoundSettings();

  /**
   * Returns true if the current user input settings are the same as what's
   * stored. If this is true, there's no need to save.
   */
  public final LiveData<Boolean> sameAsStored;

  /**
   * Returns true if the current user input is valid, so is ok to save.
   */
  public final LiveData<Boolean> inputValid = viewBoundSettings.isValid();

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
