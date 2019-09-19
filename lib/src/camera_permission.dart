import 'dart:async';
import 'package:camera_permission/src/utils/codec.dart';

import './permission_status.dart';
import 'package:flutter/services.dart';

class CameraPermission {
  static const MethodChannel _channel = const MethodChannel('camera_permission');

  Future<PermissionStatus> checkPermissionStatus() async {
    final int status = await _channel.invokeMethod('checkPermissionStatus');
    return Codec.decodePermissionStatus(status);
  }

  Future<PermissionStatus> requestPermission() async {
    final int status = await _channel.invokeMethod('requestPermission');
    return Codec.decodePermissionStatus(status);
  }

  Future<bool> openAppSettings() async {
    final bool hasOpened = await _channel.invokeMethod('openAppSettings');
    return hasOpened;
  }
}
