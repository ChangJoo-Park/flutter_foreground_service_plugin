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
    startFGS();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              RaisedButton(
                child: Text("START"),
                onPressed: () {
                  startFGS();
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

void startFGS() async {
  await FlutterForegroundPlugin.setServiceMethodInterval(seconds: 5);
  await FlutterForegroundPlugin.setServiceMethod(globalForegroundService);
  await FlutterForegroundPlugin.startForegroundService(
      holdWakeLock: false,
      onStarted: () {
        print("Foreground on Started");
      },
      onStopped: () {
        print("Foreground on Stopped");
      });
}

void globalForegroundService() {
  debugPrint("current datetime is ${DateTime.now()}");
}
