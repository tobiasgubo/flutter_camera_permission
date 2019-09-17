/// Defines the state of a permission group
class PermissionStatus {
  const PermissionStatus._(this.value);

  final int value;

  /// Permission to access the requested feature is denied by the user.
  static const PermissionStatus denied = PermissionStatus._(0);

  /// The feature is disabled (or not available) on the device.
  static const PermissionStatus disabled = PermissionStatus._(1);

  /// Permission to access the requested feature is granted by the user.
  static const PermissionStatus granted = PermissionStatus._(2);

  /// Permission to access the requested feature is denied by the OS (only on iOS).
  /// The user cannot change this app's status, possibly due to active restrictions such as
  /// parental controls being in place.
  static const PermissionStatus restricted = PermissionStatus._(3);

  /// Permission is in an unknown state
  static const PermissionStatus unknown = PermissionStatus._(4);

  static const List<PermissionStatus> values = <PermissionStatus>[
    denied,
    disabled,
    granted,
    restricted,
    unknown,
  ];

  static const List<String> _names = <String>[
    'denied',
    'disabled',
    'granted',
    'restricted',
    'unknown',
  ];

  @override
  String toString() => 'PermissionStatus.${_names[value]}';
}