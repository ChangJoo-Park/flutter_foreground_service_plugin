# flutter_foreground_service_plugin

Please *DO NOT* use now. It is experimental state.

## You Can Do this plugin with..

- Start Foreground Service (with callback)
- Stop Foreground Service (with callback)
- Using infinite interval on your configurations.


## You Can Not Do this plugin with... 

- [ ] Change Notification Level
- [ ] Change Notification title and contents


## If you use this plugin.

1. Add dependency to your pubspec.yaml


```yaml
dependencies:
    ...
    
      flutter_foreground_plugin:
        git:
          url: https://github.com/ChangJoo-Park/flutter_foreground_service_plugin

```


2. Add permission for ForegroundService to AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/> 
```


3. Add service for ForegroundService to AndroidManifest.xml below `</activity>`

```xml
<service android:name="changjoopark.com.flutter_foreground_plugin.FlutterForegroundService"/>
``` 
