//
//  CameraPermissionPlugin.m
//  camera_permission
//
//  Created by Tobias Gubo on 23.09.19.
//

#import "CameraPermissionPlugin.h"
#import <camera_permission/camera_permission-Swift.h>

@implementation CameraPermissionPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    [SwiftCameraPermissionPlugin registerWithRegistrar:registrar];
}
@end
