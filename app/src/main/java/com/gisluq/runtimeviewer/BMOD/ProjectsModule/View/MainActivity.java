package com.gisluq.runtimeviewer.BMOD.ProjectsModule.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gisluq.runtimeviewer.BMOD.ProjectsModule.Adapter.ProjectGridAdapter;
import com.gisluq.runtimeviewer.BMOD.ProjectsModule.Model.ProjectInfo;
import com.gisluq.runtimeviewer.BMOD.SystemModule.AboutActivity;
import com.gisluq.runtimeviewer.BMOD.SystemModule.LockviewActivity;
import com.gisluq.runtimeviewer.Base.BaseActivity;
import com.gisluq.runtimeviewer.Config.SystemDirPath;
import com.gisluq.runtimeviewer.Permission.PermissionsChecker;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gisluq.lib.Util.SysUtils;

public class MainActivity extends BaseActivity {

    private GridView gridView;
    private Context context;
    private List<ProjectInfo> projectInfos = null;
    private ProjectGridAdapter appGridlViewAdapter =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        gridView = (GridView)this.findViewById(R.id.activity_mian_gridview);

        projectInfos = getProjectInfos();
        appGridlViewAdapter = new ProjectGridAdapter(context,projectInfos);
        gridView.setAdapter(appGridlViewAdapter);

    }

    /**
     * 获取工程信息列表
     * @return
     */
    private List<ProjectInfo> getProjectInfos() {
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(SystemDirPath.getProjectPath(context),"folder");
        // 获取文件名列表
        List<String> fileNames = new ArrayList<>();
        if (fileInfos!=null){
            for (int i=0;i<fileInfos.size();i++){
                fileNames.add(fileInfos.get(i).FileName);
            }
        }
        Collections.sort(fileNames);//排序

        List<ProjectInfo> infos = new ArrayList<>();
        if (fileInfos!=null){

            for (int i=0;i<fileNames.size();i++){
                String name = fileNames.get(i);
                for (int j=0;j<fileInfos.size();j++){
                    FileUtils.FileInfo fileInfo = fileInfos.get(j);
                    if (fileInfo.FileName.equals(name)){
                        ProjectInfo projectInfo = new ProjectInfo();
                        projectInfo.DirName = fileInfo.FileName;
                        projectInfo.DirPath = fileInfo.FilePath;
                        infos.add(projectInfo);
                    }
                }
            }
        }
        return infos;
    }

    /***
     * 初始化应用程序状态栏显示
     * @param toolbar
     */
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setNavigationIcon(null);//设置不显示回退按钮
        getLayoutInflater().inflate(R.layout.activity_toobar_view, toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.activity_baseview_toobar_view_txtTitle);
        //判断是否为平板设备
        if (context!=null){
            boolean ispad = SysUtils.isPad(context);
            if (ispad){
                title.setPadding(20, 0, 0, 0);
            }else {
                title.setPadding(50, 0, 0, 0);
            }
            title.setText(getResources().getText(R.string.app_title_name_mian));
        }
    }

    /**
     * 初始化系统功能菜单栏
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem reItem= menu.add(Menu.NONE, Menu.FIRST + 1, 0, "刷新");
        reItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//显示到状态栏

//        MenuItem menuItemSetting= menu.add(Menu.NONE, Menu.FIRST + 2, 0, "锁屏设置");
        MenuItem menuItemAbout= menu.add(Menu.NONE, Menu.FIRST + 3, 0, "关于");

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())//得到被点击的item的itemId
        {
            case Menu.FIRST+1: //对应的ID就是在add方法中所设定的Id
                RefreshTask refreshTask =new RefreshTask(context);
                refreshTask.execute();
                break;
//            case Menu.FIRST+2: //对应的ID就是在add方法中所设定的Id
//                Intent lockIntent = new Intent(context, LockviewActivity.class);
//                context.startActivity(lockIntent);
//                break;
            case Menu.FIRST+3: //对应的ID就是在add方法中所设定的Id
                Intent aboutIntent = new Intent(context, AboutActivity.class);
                context.startActivity(aboutIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出系统
     */
    private void exitActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否退出应用程序？");
        builder.setTitle("系统提示");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        });
        builder.create().show();

    }

    /**
     * 刷新项目
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public class RefreshTask extends AsyncTask<Integer, Integer, Boolean> {

        private Context context;
        private ProgressDialog progressDialog;//等待对话框

        public RefreshTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            //第一个执行方法
            super.onPreExecute();
            progressDialog= ProgressDialog.show(context, null, "工程列表刷新...");
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            SystemClock.sleep(1000);
            try{
                projectInfos = getProjectInfos();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result){
                appGridlViewAdapter.setAdapterList(projectInfos);//重新设置任务列表
                appGridlViewAdapter.refreshData();//刷新数据
                Toast.makeText(context, "工程列表刷新成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "工程列表刷新失败", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
