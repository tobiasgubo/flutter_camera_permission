import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:camera_permission/camera_permission.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  void _checkCameraPermission() async {
    PermissionStatus permissionStatus = await CameraPermission().checkPermissionStatus();
    print(permissionStatus);
  }

  void _requestCameraPermission() async {
    PermissionStatus permissionStatus = await CameraPermission().requestPermission();
    print(permissionStatus);
  }

  void _openSettings() {
    CameraPermission().openAppSettings();
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
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              RaisedButton(
                onPressed: _checkCameraPermission,
                child: Text("Check Camera Permission"),
              ),
              RaisedButton(
                onPressed: _requestCameraPermission,
                child: Text("Request Camera Permission"),
              ),
              RaisedButton(
                onPressed: _openSettings,
                child: Text("Open Settings"),
              ),
            ]
          ),
        ),
      ),
    );
  }
}
