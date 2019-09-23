//
//  PermissionEnums.swift
//  camera_permission
//
//  Created by Tobias Gubo on 23.09.19.
//

import Foundation

enum PermissionStatus : Int {
    case PermissionStatusDenied = 0
    case PermissionStatusDisabled = 1
    case PermissionStatusGranted = 2
    case PermissionStatusRestricted = 3
    case PermissionStatusUnknown = 4
}
