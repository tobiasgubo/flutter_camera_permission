import Flutter
import UIKit
import AVFoundation

public class SwiftCameraPermissionPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "camera_permission", binaryMessenger: registrar.messenger())
    let instance = SwiftCameraPermissionPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    NSLog("hi")
    NSLog(String(PermissionStatus.PermissionStatusDenied.rawValue))
    
    switch call.method {
    case "checkPermissionStatus":
        self.checkPermissionStatus(result: result)
        break
    case "requestPermission":
        self.requestPermission(result: result)
        break
    case "openAppSettings":
        self.openAppSettings(result: result)
        break
    default:
        result(FlutterMethodNotImplemented)
    }
  }
    
    private func checkPermissionStatus(result: FlutterResult) {
        result(self.getPermissionStatus().rawValue)
    }
    
    private func requestPermission(result: @escaping FlutterResult) {
        let permissionStatus = self.getPermissionStatus()
        if (permissionStatus != PermissionStatus.PermissionStatusGranted) {
            AVCaptureDevice.requestAccess(
            for: .video) { (granted) in
                if (granted) {
                    result(PermissionStatus.PermissionStatusGranted.rawValue)
                }else {
                    result(PermissionStatus.PermissionStatusDenied.rawValue)
                }
            }
        } else {
            result(permissionStatus.rawValue)
        }
    }
    
    private func getPermissionStatus() -> PermissionStatus {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            return PermissionStatus.PermissionStatusGranted
            
        case .notDetermined:
            return PermissionStatus.PermissionStatusDenied
            
        case .denied:
            return PermissionStatus.PermissionStatusDenied
            
        case .restricted:
            return PermissionStatus.PermissionStatusRestricted
            
        default:
            return PermissionStatus.PermissionStatusUnknown
        }
    }
    
    private func openAppSettings(result: @escaping FlutterResult) {
        result(false);
    }
}
