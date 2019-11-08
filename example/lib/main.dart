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
    super.initState();
    startForegroundService();
  }

  Future<void> startForegroundService() async {
    await FlutterForegroundPlugin.startForegroundService(
        globalForegroundSerivce);
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
              Text('Running on: $_platformVersion\n'),
              RaisedButton(
                child: Text("START"),
                onPressed: () async {
                  await FlutterForegroundPlugin.startForegroundService();
                },
              ),
              RaisedButton(
                child: Text("STOP"),
                onPressed: () async {
                  await FlutterForegroundPlugin.stopForegroundService();
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}

void globalForegroundSerivce() {
  debugPrint("current datetime is ${DateTime.now()}");
}
