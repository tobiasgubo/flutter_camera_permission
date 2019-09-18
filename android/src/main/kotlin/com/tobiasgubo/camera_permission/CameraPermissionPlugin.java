package com.tobiasgubo.camera_permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.IntDef;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class CameraPermissionPlugin implements MethodCallHandler {
    private static final String LOG_TAG = "camera_permission";
    private static final int PERMISSION_CODE = 947;


    private CameraPermissionPlugin(Registrar mRegistrar) {
        this.mRegistrar = mRegistrar;
    }

    //PERMISSION_STATUS
    private static final int PERMISSION_STATUS_DENIED = 0;
    private static final int PERMISSION_STATUS_DISABLED = 1;
    private static final int PERMISSION_STATUS_GRANTED = 2;
    private static final int PERMISSION_STATUS_RESTRICTED = 3;
    private static final int PERMISSION_STATUS_UNKNOWN = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PERMISSION_STATUS_DENIED,
            PERMISSION_STATUS_DISABLED,
            PERMISSION_STATUS_GRANTED,
            PERMISSION_STATUS_RESTRICTED,
            PERMISSION_STATUS_UNKNOWN,
    })
    private @interface PermissionStatus {
    }


    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "camera_permission");
        final CameraPermissionPlugin cameraPermissionPlugin = new CameraPermissionPlugin(registrar);
        channel.setMethodCallHandler(cameraPermissionPlugin);

        registrar.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
            @Override
            public boolean onRequestPermissionsResult(int id, String[] permissions, int[] grantResults) {
                if (id == PERMISSION_CODE) {
                    //cameraPermissionPlugin.handlePermissionsRequest(permissions, grantResults);
                    cameraPermissionPlugin.handlePermissionsResponse(permissions, grantResults);
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private final Registrar mRegistrar;
    private Result mResult;
    private ArrayList<String> mRequestedPermissions;
    private int mRequestResult;

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "checkPermissionStatus": {
                @PermissionStatus final int permissionStatus = checkCameraPermissionStatus();

                result.success(permissionStatus);
                break;
            }
            case "requestPermission":
                if (mResult != null) {
                    result.error(
                            "ERROR_ALREADY_REQUESTING_PERMISSIONS",
                            "A request for permissions is already running, please wait for it to finish before doing another request (note that you can request multiple permissions at the same time).",
                            null);
                    return;
                }

                mResult = result;
                requestCameraPermission();
                break;
            case "openAppSettings":
                boolean isOpen = openAppSettings();
                result.success(isOpen);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void requestCameraPermission() {
        if (hasPermissionInManifest(Manifest.permission.CAMERA)) {
            String[] permissionNames = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(mRegistrar.activity(), permissionNames, PERMISSION_CODE);
        } else {
            Log.e(LOG_TAG, "Android Manifest does not contain the Camera Permission!");
        }
    }

    private void handlePermissionsResponse(String[] permissions, int[] grantResults) {

        if (responseIsValid(permissions, grantResults)) {
            mRequestResult = toPermissionStatus(grantResults[0]);
            processResult();
        } else {
            Log.e(LOG_TAG, "Invalid Permissions Request!");
        }

    }
    private boolean responseIsValid(String[] permissions, int[] grantResults){
        if (permissions.length != 1)
            return false;

        if (!permissions[0].equals(Manifest.permission.CAMERA))
            return false;

        if (grantResults.length != 1)
            return false;

        return true;
    }

    private void processResult() {
        mResult.success(mRequestResult);
        mResult = null;
    }

    private int checkCameraPermissionStatus () {
        if (hasPermissionInManifest(Manifest.permission.CAMERA)) {
            final Context context = mRegistrar.activity() == null ? mRegistrar.activeContext() : mRegistrar.activity();
            if (context == null) {
                Log.d(LOG_TAG, "Unable to detect current Activity or App Context.");
                return PERMISSION_STATUS_UNKNOWN;
            }

            final boolean targetsMOrHigher = context.getApplicationInfo().targetSdkVersion >= VERSION_CODES.M;
                if (targetsMOrHigher) {
                    final int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

                    if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                        return PERMISSION_STATUS_DENIED;
                    } else if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                        return PERMISSION_STATUS_UNKNOWN;
                    }

                    return PERMISSION_STATUS_GRANTED;
                }
        }

        return PERMISSION_STATUS_DENIED;
    }

    @PermissionStatus
    private int toPermissionStatus(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED ? PERMISSION_STATUS_GRANTED : PERMISSION_STATUS_DENIED;
    }

    private boolean openAppSettings() {
        final Context context = mRegistrar.activity() == null ? mRegistrar.activeContext() : mRegistrar.activity();
        if (context == null) {
            Log.e(LOG_TAG, "Unable to detect current Activity or App Context.");
            return false;
        }

        try {
            Intent settingsIntent = new Intent();
            settingsIntent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
            settingsIntent.setData(android.net.Uri.parse("package:" + context.getPackageName()));
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

            context.startActivity(settingsIntent);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean hasPermissionInManifest(String permission) {
        try {
            if (mRequestedPermissions != null) {
                for (String r : mRequestedPermissions) {
                    if (r.equals(permission)) {
                        return true;
                    }
                }
            }

            final Context context = mRegistrar.activity() == null ? mRegistrar.activeContext() : mRegistrar.activity();

            if (context == null) {
                Log.e(LOG_TAG, "Unable to detect current Activity or App Context.");
                return false;
            }

            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            if (info == null) {
                Log.e(LOG_TAG, "Unable to get Package info, will not be able to determine permissions to request.");
                return false;
            }

            mRequestedPermissions = new ArrayList<>(Arrays.asList(info.requestedPermissions));
            for (String r : mRequestedPermissions) {
                if (r.equals(permission)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Unable to check manifest for permission: ", ex);
        }
        return false;
    }

}

