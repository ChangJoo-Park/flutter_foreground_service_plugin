import 'dart:async';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class FlutterForegroundPlugin {
  static const MethodChannel _mainChannel =
      const MethodChannel('com.changjoopark.flutter_foreground_plugin/main');

  static Future<void> startForegroundService(
      [Function serviceMethod, bool holdWakeLock = false]) async {
    final int setupCallbackHandle = PluginUtilities.getCallbackHandle(
            _setupForegroundServiceCallbackChannel)
        .toRawHandle();

    await _mainChannel.invokeMethod(
        "startForegroundService", <dynamic>[setupCallbackHandle, holdWakeLock]);

    if (serviceMethod != null) {
      setServiceMethod(serviceMethod);
    }
    print("startForegroundService");
  }

  static Future<void> stopForegroundService() async {
    await _mainChannel.invokeMethod("stopForegroundService");
  }

  static Future<void> setServiceMethod(Function serviceMethod) async {
    final serviceMethodHandle =
        PluginUtilities.getCallbackHandle(serviceMethod).toRawHandle();

    await _mainChannel
        .invokeMethod("setServiceMethodHandle", <dynamic>[serviceMethodHandle]);
  }
}

//the android side will use this function as the entry point
//for the background isolate that will be used to execute dart handles
void _setupForegroundServiceCallbackChannel() {
  const MethodChannel _callbackChannel = MethodChannel(
      "com.changjoopark.flutter_foreground_plugin/main", JSONMethodCodec());

  WidgetsFlutterBinding.ensureInitialized();

  _callbackChannel.setMethodCallHandler((MethodCall call) async {
    final dynamic args = call.arguments;
    final CallbackHandle handle = CallbackHandle.fromRawHandle(args[0]);

    PluginUtilities.getCallbackFromHandle(handle)();
  });
}
