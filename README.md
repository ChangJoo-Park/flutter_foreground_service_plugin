# flutter_foreground_service_plugin

## You Can Do this plugin with..

- Start Foreground Service (with callback)
- Stop Foreground Service (with callback)
- Using infinite interval on your configurations
- Change Notification title and contents

## You Can Not Do this plugin with...

- [ ] Change Notification Level



## If you use this plugin.

1. Add dependency to your pubspec.yaml


```yaml
dependencies:
    ...

  flutter_foreground_plugin: ^0.4.0
```

2. Add `xmlns:tools` under `<manifest xmlns:android="http://schemas.android.com/apk/res/android"`
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
```

3. Add permission for ForegroundService to AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```


4. Add service for ForegroundService to AndroidManifest.xml below `</activity>`

```xml
<service android:name="changjoopark.com.flutter_foreground_plugin.FlutterForegroundService"/>
```

5. Add use-sdk under `application`

```
    <uses-sdk
        android:minSdkVersion="23"
        tools:overrideLibrary="changjoopark.com.flutter_foreground_plugin" />
```

6. Add icon image for notification.

[Notification Icon Generator](https://romannurik.github.io/AndroidAssetStudio/icons-notification.html#source.type=clipart&source.clipart=ac_unit&source.space.trim=1&source.space.pad=0&name=ic_stat_ac_unit) will be helpful.

path: `android/app/src/main/res/drawable-*`

7. Write code for foreground service

```dart
void main() {
  runApp(MyApp());
  startForegroundService();

  // if you need to stop foreground service,
  // await FlutterForegroundPlugin.stopForegroundService();
}

void startForegroundService() async {
  await FlutterForegroundPlugin.setServiceMethodInterval(seconds: 5);
  await FlutterForegroundPlugin.setServiceMethod(globalForegroundService);
  await FlutterForegroundPlugin.startForegroundService(
    holdWakeLock: false,
    onStarted: () {
      print("Foreground on Started");
    },
    onStopped: () {
      print("Foreground on Stopped");
    },
    title: "Flutter Foreground Service",
    content: "This is Content",
    iconName: "ic_stat_hot_tub",
  );
}

void globalForegroundService() {
  debugPrint("current datetime is ${DateTime.now()}");
}
```
