package com.gisluq.runtimeviewer.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

/**
 * 用户权限管理类
 * Created by luq on 2017/4/30.
 */

public class PermissionsUtils {

    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static PermissionsChecker mPermissionsChecker; // 权限检测器

    /**
     * 权限检查
     * @param context
     */
    public static void PermissionsChecker(Context context){
        mPermissionsChecker = new PermissionsChecker(context);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult((Activity)context, REQUEST_CODE, PERMISSIONS);
        }
    }

}
