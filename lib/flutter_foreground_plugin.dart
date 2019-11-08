import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';

class FlutterForegroundPlugin {
  static const MethodChannel _mainChannel =
      const MethodChannel('com.changjoopark.flutter_foreground_plugin/main');

  static const MethodChannel _callbackChannel = const MethodChannel(
      'com.changjoopark.flutter_foreground_plugin/callback');

  static int serviceCallbackHandle = -1;

  static Future<void> startForegroundService(
      [bool holdWakeLock = false]) async {
    await _mainChannel.invokeMethod("startForegroundService",
        <String, dynamic>{'holdWakeLock': holdWakeLock});
  }

  static Future<void> stopForegroundService() async {
    await _mainChannel.invokeMethod("stopForegroundService");
  }

  static Future<void> setServiceMethod(Function serviceMethod) async {
    final serviceMethodHandle =
        PluginUtilities.getCallbackHandle(serviceMethod).toRawHandle();

    _callbackChannel.setMethodCallHandler(_onForegroundServiceCallback);

    await _mainChannel
        .invokeMethod("setServiceMethodHandle", <String, dynamic>{'serviceMethodHandle': serviceMethodHandle});
  }

  static Future<void> setServiceMethodInterval({int seconds = 5}) async {
    await _mainChannel
        .invokeMethod("setServiceMethodInterval", <String, dynamic>{
      'seconds': seconds,
    });
  }

  static Future<void> _onForegroundServiceCallback(MethodCall call) async {
    switch(call.method) {
      case "onStarted":
        break;
      case "onStopped":
        break;
      case "onServiceMethodCallback":
        final CallbackHandle handle = CallbackHandle.fromRawHandle(call.arguments);
        if (handle != null) {
          PluginUtilities.getCallbackFromHandle(handle)();
        }
        break;
      default:
        break;
    }

  }
}
