package anb.schedule.notifications;

import anb.schedule.notifications.CustomExceptionHandler;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.lang.ref.WeakReference;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class ScheduleNotifications extends CordovaPlugin {

  private Context context;
  private static WeakReference<CordovaWebView> webView = null;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    Context appContext = cordova.getActivity();
    context = appContext;
    ScheduleNotifications.webView = new WeakReference<CordovaWebView>(webView);

    Thread.setDefaultUncaughtExceptionHandler(
      new CustomExceptionHandler(context)
    );
  }

  @Override
  protected void pluginInitialize() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Create a notification channel for Android 8.0 and above
      createNotificationChannel();

      if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
      }
    }
  }

  @Override
  public boolean execute(
    String action,
    JSONArray args,
    CallbackContext callbackContext
  ) throws JSONException {
    if (action.equals("requestPermission")) {
      this.requestPermission(callbackContext);
      return true;
    }
    if (action.equals("sendInstantNotification")) {
      this.sendInstantNotification(args, callbackContext);
      return true;
    }
    return false;
  }

  public void requestPermission(CallbackContext callbackContext) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Create a notification channel for Android 8.0 and above
      createNotificationChannel();

      if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
        callbackContext.success("Notification already enabled");
      } else {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
      }
    }
  }

  public void sendInstantNotification(
    JSONArray args,
    CallbackContext callbackContext
  ) {
    int resId = this.getResId("res://drawable-ldpi-v4/pw_notification.png");
    if (resId < 1) {
      resId = android.R.drawable.ic_popup_reminder;
    }
    JSONObject arg_object = args.optJSONObject(0);
    String title = arg_object.optString("title", "");
    String message = arg_object.optString("message", "");

    NotificationCompat.Builder builder = new NotificationCompat.Builder(
      context,
      "anb_local_channel"
    )
      .setSmallIcon(resId)
      .setContentTitle(title)
      .setContentText(message);
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
      context
    );

    notificationManager.notify(1, builder.build());
    callbackContext.success("Notification sent successfully");
  }

  private int getResId(String resPath) {
    Resources res = context.getResources();
    String pkgName = getPkgName(res);
    String resName = getBaseName(resPath);
    int resId;

    resId = res.getIdentifier(resName, "mipmap", pkgName);
    Log.d("resId", " " + resId);
    if (resId == 0) {
      resId = res.getIdentifier(resName, "drawable", pkgName);
    }
    Log.d("resId", " " + resId);
    if (resId == 0) {
      resId = res.getIdentifier(resName, "raw", pkgName);
    }
    Log.d("resId", " " + resId);
    return resId;
  }

  private void createNotificationChannel() {
    String channelId = "anb_local_channel";

    CharSequence channelName = "Anb Notifications";

    String channelDescription =
      "Notification channel for new anb new mobile banking app";

    int importance = NotificationManager.IMPORTANCE_HIGH;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        channelName,
        importance
      );
      channel.setDescription(channelDescription);

      channel.enableVibration(true);

      channel.setShowBadge(false);

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(
        NotificationManager.class
      );
      notificationManager.createNotificationChannel(channel);
    }
  }

  private String getPkgName(Resources res) {
    Log.d(
      "res == Resources.getSystem() ? android : context.getPackageName();",
      res == Resources.getSystem() ? "android" : context.getPackageName()
    );
    return res == Resources.getSystem() ? "android" : context.getPackageName();
  }

  private String getBaseName(String resPath) {
    String drawable = resPath;

    if (drawable.contains("/")) {
      drawable = drawable.substring(drawable.lastIndexOf('/') + 1);
    }

    if (resPath.contains(".")) {
      drawable = drawable.substring(0, drawable.lastIndexOf('.'));
    }
    Log.d("drawable: ", drawable);
    return drawable;
  }
}
