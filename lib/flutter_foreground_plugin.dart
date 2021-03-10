import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';

class FlutterForegroundPlugin {
  static const MethodChannel _mainChannel =
      const MethodChannel('com.changjoopark.flutter_foreground_plugin/main');

  static const MethodChannel _callbackChannel = const MethodChannel(
      'com.changjoopark.flutter_foreground_plugin/callback');

  static Function? onStartedMethod;
  static Function? onStoppedMethod;

  /// [startForegroundService]
  ///
  static Future<void> startForegroundService({
    bool holdWakeLock = false,
    Function? onStarted,
    Function? onStopped,
    required String iconName,
    int color = 0,
    required String title,
    String content = "",
    String subtext = "",
    bool chronometer = false,
    bool stopAction = false,
    String? stopIcon,
    String stopText = 'Close',
  }) async {
    if (onStarted != null) {
      onStartedMethod = onStarted;
    }

    if (onStopped != null) {
      onStoppedMethod = onStopped;
    }

    await _mainChannel.invokeMethod("startForegroundService", <String, dynamic>{
      'holdWakeLock': holdWakeLock,
      'icon': iconName,
      'color': color,
      'title': title,
      'content': content,
      'subtext': subtext,
      'chronometer': chronometer,
      'stop_action': stopAction,
      'stop_icon': stopIcon,
      'stop_text': stopText,
    });
  }

  static Future<void> stopForegroundService() async {
    await _mainChannel.invokeMethod("stopForegroundService");
  }

  static Future<void> setServiceMethod(Function serviceMethod) async {
    final serviceMethodHandle =
        PluginUtilities.getCallbackHandle(serviceMethod)!.toRawHandle();

    _callbackChannel.setMethodCallHandler(_onForegroundServiceCallback);

    await _mainChannel.invokeMethod("setServiceMethodHandle",
        <String, dynamic>{'serviceMethodHandle': serviceMethodHandle});
  }

  static Future<void> setServiceMethodInterval({int seconds = 5}) async {
    await _mainChannel
        .invokeMethod("setServiceMethodInterval", <String, dynamic>{
      'seconds': seconds,
    });
  }

  static Future<void> _onForegroundServiceCallback(MethodCall call) async {
    switch (call.method) {
      case "onStarted":
        if (onStartedMethod != null) {
          onStartedMethod!();
        }
        break;
      case "onStopped":
        if (onStoppedMethod != null) {
          onStoppedMethod!();
        }
        break;
      case "onServiceMethodCallback":
        final CallbackHandle handle =
            CallbackHandle.fromRawHandle(call.arguments);
        PluginUtilities.getCallbackFromHandle(handle)!();
        break;
      default:
        break;
    }
  }
}
