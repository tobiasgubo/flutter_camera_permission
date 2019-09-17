#import "CameraPermissionPlugin.h"
#import <camera_permission/camera_permission-Swift.h>

@implementation CameraPermissionPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftCameraPermissionPlugin registerWithRegistrar:registrar];
}
@end
