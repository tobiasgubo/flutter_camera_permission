import '../../camera_permission.dart';

class Codec {
  static PermissionStatus decodePermissionStatus (int value) {
    return PermissionStatus.values[value];
  }
}