package com.notgov.rebootcocoa;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Service for rebooting the COCOA app.
 */
public class RebootCocoaService extends IntentService {

  private static final String TAG = "KillAndBoot";

  // Package ID for the COCOA app.
  private static final String COCOA_PACKAGE_ID = "jp.go.mhlw.covid19radar";

  public RebootCocoaService() {
    super(RebootCocoaService.class.getSimpleName());
  }

  private final Handler handler = new Handler();

  @Override
  protected void onHandleIntent(Intent intent) {
    killCocoa();
    // Boot COCOA, but only after a bit of delay. Not sure if this delay is
    // really needed, but I'm not confident that killing the app actually kills
    // it immediately.
    handler.postDelayed(this::bootCocoa, 500);
  }

  /**
   * Kills the COCOA app.
   */
  private void killCocoa() {
    ActivityManager am =
        (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
    am.killBackgroundProcesses(COCOA_PACKAGE_ID);
  }

  /**
   * Boots the COCOA app.
   */
  private void bootCocoa() {
    Intent bootCocoaIntent =
        this.getPackageManager().getLaunchIntentForPackage(
            COCOA_PACKAGE_ID);
    this.startActivity(bootCocoaIntent);
  }

  /**
   * Utility for constructing a {@link PendingIntent} for starting the
   * {@link RebootCocoaService}, which can be used to reboot COCOA.
   */
  public static PendingIntent constructRebootIntent(Context context) {
    Intent bootIntent = new Intent(context, RebootCocoaService.class);
    return PendingIntent.getService(
        context,
        0,
        bootIntent,
        PendingIntent.FLAG_ONE_SHOT);
  }
}
