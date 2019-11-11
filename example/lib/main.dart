import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_foreground_plugin/flutter_foreground_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    startForegroundService();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Foreground Service Example'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              RaisedButton(
                child: Text("START"),
                onPressed: () {
                  startForegroundService();
                },
              ),
              RaisedButton(
                child: Text("STOP"),
                onPressed: () async {
                  await FlutterForegroundPlugin.stopForegroundService();
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
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
