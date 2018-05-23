package net.coconauts.notificationListener;

import android.view.Gravity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import org.apache.cordova.PluginResult;
import android.service.notification.StatusBarNotification;
import android.os.Bundle;
import java.util.Set;

public class NotificationCommands extends CordovaPlugin {

    private static final String TAG = "NotificationCommands";

    private static final String LISTEN = "listen";

    // note that webView.isPaused() is not Xwalk compatible, so tracking it poor-man style
    private boolean isPaused;

    private static CallbackContext listener;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

      Log.i(TAG, "Received action " + action);

      if (LISTEN.equals(action)) {
        setListener(callbackContext);
        return true;
      } else {
        callbackContext.error(TAG+". " + action + " is not a supported function.");
        return false;
      }
    }

    @Override
    public void onPause(boolean multitasking) {
      this.isPaused = true;
    }

    @Override
    public void onResume(boolean multitasking) {
      this.isPaused = false;
    }

    public void setListener(CallbackContext callbackContext) {
      Log.i("Notification", "Attaching callback context listener " + callbackContext);
      listener = callbackContext;

      PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
      result.setKeepCallback(true);
      callbackContext.sendPluginResult(result);
    }

    public static void notifyListener(StatusBarNotification n){
      if (listener == null) {
        Log.e(TAG, "Must define listener first. Call notificationListener.listen(success,error) first");
        return;
      }
      try  {

        JSONObject json = parse(n);

        PluginResult result = new PluginResult(PluginResult.Status.OK, json);

        Log.i(TAG, "Sending notification to listener " + json.toString());
        result.setKeepCallback(true);

        listener.sendPluginResult(result);
      } catch (Exception e){
        Log.e(TAG, "Unable to send notification "+ e);
        listener.error(TAG+". Unable to send message: "+e.getMessage());
      }
    }


    private static JSONObject parse(StatusBarNotification n)  throws JSONException{

        JSONObject json = new JSONObject();

        Bundle extras = n.getNotification().extras;
        json.put("package", n.getPackageName());

        Set<String> keys = extras.keySet();
        /* Iterate over all keys of the bundle to give back all the information available */
        for (String key : keys) {
            try {
              String printKey = key;
              /* If key has a prefix android., this will be removed. */
              if(printKey.indexOf("android.")==0 && printKey.length()>8){
                printKey = printKey.substring(8,key.length());
              }
              // json.put(key, bundle.get(key)); see edit below
              json.put(printKey, JSONObject.wrap(extras.get(key)));
            } catch(JSONException e) {
                Log.d(TAG,e.getMessage());
            }
        }

        return json;
    }

}
