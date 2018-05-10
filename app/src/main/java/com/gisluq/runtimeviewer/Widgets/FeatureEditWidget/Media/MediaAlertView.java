package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Media;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;
import com.gisluq.runtimeviewer.Utils.TimeUtils;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter.FieldAdapter;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter.FileAdapter;

import java.util.List;

import gisluq.lib.Util.SysUtils;
import gisluq.lib.Util.ToastUtils;


/**
 * 多媒体
 * Created by lq on 2015/11/24.
 */
public class MediaAlertView {

    private Context context;
    private String photoPath;
    private String videoPath;
    private String audioPath;

    private ListView listView;//文件列表
    private FileAdapter fileAdapter;
    private List<FileUtils.FileInfo> fileList;

    private AlertDialog alertDialog = null;//状态窗口

    public MediaAlertView(Context context, String p, String v, String a) {
        this.context = context;
        this.photoPath = p;
        this.videoPath = v;
        this.audioPath = a;

        alertDialog = new AlertDialog.Builder(context).create();
        final View view =  LayoutInflater.from(context).inflate( R.layout.widget_view_feature_edit_alert_mediafiles, null);
        listView = view.findViewById(R.id.widget_view_feature_edit_alert_mediafiles_lsFiles);
        RadioButton radioButtonPhoto = view.findViewById(R.id.widget_view_feature_edit_alert_mediafiles_rbPhoto);
        RadioButton radioButtonVideo = view.findViewById(R.id.widget_view_feature_edit_alert_mediafiles_rbVideo);
        RadioButton radioButtonVoice = view.findViewById(R.id.widget_view_feature_edit_alert_mediafiles_rbVoice);

        fileList = FileUtils.getFileListInfo(photoPath,"all");
        fileAdapter = new FileAdapter(context,fileList);
        listView.setAdapter(fileAdapter);

        alertDialog.setView(view);

        radioButtonPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fileList = FileUtils.getFileListInfo(photoPath,"all");
                    fileAdapter.setAdapterList(fileList);
                    fileAdapter.refreshData();
                }
            }
        });

        radioButtonVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fileList = FileUtils.getFileListInfo(videoPath,"all");
                    fileAdapter.setAdapterList(fileList);
                    fileAdapter.refreshData();
                }
            }
        });

        radioButtonVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fileList = FileUtils.getFileListInfo(audioPath,"all");
                    fileAdapter.setAdapterList(fileList);
                    fileAdapter.refreshData();
                }
            }
        });

    }


    /**
     * 显示
     */
    public void show(){
        alertDialog.show();
        if (SysUtils.isPad(context)){
            //设置弹窗的固定宽高
            WindowManager m = ((Activity)context).getWindowManager();
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
            WindowManager.LayoutParams p = alertDialog.getWindow().getAttributes();  //获取对话框当前的参数值
            p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.6
            p.width = (int) (d.getWidth() * 0.5);    //宽度设置为屏幕的0.6
            alertDialog.getWindow().setAttributes(p);
        }
    }

    /**
     * 隐藏
     */
    public void hide(){
        alertDialog.hide();
    }


}
