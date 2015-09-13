# NotificationListenerService plugin for Cordova

This is an implementation of the
[NotificationListenerService in Android](https://developer.android.com/reference/android/service/notification/NotificationListenerService.html)
for Cordova.

    A service that receives calls from the system when new notifications are posted or removed, or their ranking changed.

Note: This plugin doesn't work for IOS or Windows Phone, feel free to create a pull request if you want to add that functionality to this project.

## How to install

    cordova plugin add https://github.com/coconauts/NotificationListener-cordova

## Enable notification listener service

This service requires an special permission that must be enabled from settings on Android (Settings > Notifications > Notification access)

![](/settings.jpg)

Note: The app requires the following permission in your Manifest file on Android, which will be added automatically:

    android.permission.BIND_NOTIFICATION_LISTENER_SERVICE

## How to use

On Cordova initialization, add the callback for your notification-listener.
Then everytime you get a notification in your phone that callback in JS will be triggered with the notification data.

```
var app = {
    initialize: function() {
       console.log("Initializing app");
       this.bindEvents();
    },
    bindEvents: function() {
        console.log("Binding events");
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    onDeviceReady: function() {
       console.log("Device ready");

       notificationListener.listen(function(n){
         console.log("Received notification " + JSON.stringify(n) );
       }, function(e){
         console.log("Notification Error " + e);
       });
    }
};
app.initialize();
```

For a full example, please see our [WatchDuino2 repository](https://github.com/coconauts/watchduino2-companion-app)

## Sample output
```
Received notification
{
  "title":"Chuck Norris",
  "package":"com.google.android.talk",
  "text":"Hello world",
  "textLines":""
}
```

## Notification response format

The notification response received by Javascript is a simplified object from the
[StatusBarNotification class](https://developer.android.com/reference/android/service/notification/StatusBarNotification.html)
in Android.

Feel free to update the
 [notification parser](https://github.com/coconauts/NotificationListener-cordova/blob/master/src/android/NotificationCommands.java#L80) 
 inside this plugin if needed.
