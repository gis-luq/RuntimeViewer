package com.gisluq.runtimeviewer.Config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.gisluq.runtimeviewer.Common.DialogTools;
import com.gisluq.runtimeviewer.Utils.FileUtils;


/**
 *文件工作空间初始化类
 */
public class AppWorksSpaceInit {

    private static String Tag = "AppWorksSpaceInit";
    private static boolean isWriteStorage = true;//默认可读写

    /**
     * 初始化工作空间
     * @param context
     */
    public static boolean init(final Context context){

        /*** 系统内部存储 *******************************************************************************************/
        String WorkSpacePath= SystemDirPath.getMainWorkSpacePath(context);
        if(!FileUtils.isExist(WorkSpacePath)){
            boolean isCreateWorkSpacePath = FileUtils.createChildFilesDir(WorkSpacePath); //创建主目录文件夹
            if(isCreateWorkSpacePath){
                Log.d(Tag, "目录：" + WorkSpacePath + " 创建成功");
            }else{
                Log.d(Tag, "目录："+WorkSpacePath + " 创建失败");
                isWriteStorage = false;
                DialogTools.showSystemDialog(context,"系统提示",WorkSpacePath + " 创建失败,请检查APP是否具备写入权限","确定",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((Activity)context).finish();
                    }
                },null,null);
            }
        }

        String Projects = SystemDirPath.getProjectPath(context);
        if(!FileUtils.isExist(Projects)){
            boolean isCreateProjects = FileUtils.createChildFilesDir(Projects); //创建工程目录文件夹
            if(isCreateProjects){
                Log.d(Tag, "目录："+Projects + " 创建成功");
            }else{
                Log.d(Tag, "目录："+Projects + " 创建失败");
            }
        }

        String SysConf = SystemDirPath.getSystemConfPath(context);
        if(!FileUtils.isExist(SysConf)){
            boolean isCreateProjects = FileUtils.createChildFilesDir(SysConf); //创建系统配置目录文件夹
            if(isCreateProjects){
                Log.d(Tag, "目录："+Projects + " 创建成功");
            }else{
                Log.d(Tag, "目录："+Projects + " 创建失败");
            }
        }

        return isWriteStorage;
    }

}
