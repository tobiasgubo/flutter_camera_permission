import 'package:camera_permission/camera_permission.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:camera_permission/src/camera_permission.dart';

void main() {
  const MethodChannel channel = MethodChannel('camera_permission');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      switch(methodCall.method) {
        case "checkPermissionStatus":
          return 1;
          break;
        case "requestPermission":
          return 0;
          break;
        case "openAppSettings":
          return true;
          break;
        default:
          return null;
      }
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('checkPermissionStatus', () async {
    expect(await CameraPermission().checkPermissionStatus(), PermissionStatus.disabled);
  });

  test('requestPermission', () async {
    expect(await CameraPermission().requestPermission(), PermissionStatus.denied);
  });

  test('requestPermission', () async {
    expect(await CameraPermission().openAppSettings(), true);
  });
}
