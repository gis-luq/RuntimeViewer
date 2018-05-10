package com.gisluq.runtimeviewer.Config;


import android.content.Context;

import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;

import gisluq.lib.Util.SDCardUtils;

/**
 * 系统文件夹管理类
 * 记录、获取系统文件夹路径，管理存储位置信息
 */
public class SystemDirPath {
    private static String MainWorkSpace = "/RuntimeViewer";//工作空间地址

    private static String Projects = "/Projects"; //系统工程文件夹

    private static String SystemConf = "/System"; //系统模板
    private static String lockViewConf = "/lockscreen.conf"; //锁屏配置文件信息
//    private static String licenseCode = "/license.applic";//许可配置文件

    public static String SDPath = SDCardUtils.getSDCardPath();//系统SD卡路径

    public static ConfigEntity configEntity = null;
    /**
     * 获取SD卡工具路径
     * @return
     */
    public static String getSDPath(){
        return SDPath;
    }

    /**
     * 获取系统工作空间文件夹路径（主目录）
     * 主目录以系统内部存储为主
     * @return
     * @param context
     */
    public static String getMainWorkSpacePath(Context context){
        if (configEntity==null){
            configEntity =AppConfig.getConfig(context);
        }
        String path = configEntity.getWorkspacePath();
        if (path!=null||(!path.equals(""))){
            return SDPath + path;
        }
        return  SDPath + MainWorkSpace;
    }

    /**
     * 获取系统配置文件夹路径（系统配置目录仅内部存储）
     * @return
     */
    public static String getSystemConfPath(Context context){
        return getMainWorkSpacePath(context) + SystemConf;
    }


    /**
     * 获取工程文件夹路径
     * @return
     */
    public static String getProjectPath(Context context){
        return  getMainWorkSpacePath(context) + Projects;
    }


    /**
     * 获取系统锁屏配置文件路径
     * @return
     */
    public static String getLockViewConfPath(Context context){
        return  getMainWorkSpacePath(context)+ SystemConf + lockViewConf;
    }


}
