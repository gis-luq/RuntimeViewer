package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter.FeatureTempleteAdapter;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter.FieldAdapter;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Listener.MapSelectOnTouchListener;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Media.MediaAlertView;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Media.VoiceAlertView;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Resource.DrawToolsResource;

import java.io.File;

import gisluq.lib.Util.DateUtils;
import gisluq.lib.Util.SysUtils;
import gisluq.lib.Util.ToastUtils;

/**
 * 编辑组件
 * Created by gis-luq on 2018/4/25.
 */
public class FeatureEditWidget extends BaseWidget {

    public View mWidgetView = null;
    public DrawToolsResource drawToolsResource =null;//编辑工具

    private SketchEditor mainSketchEditor;
    private SketchStyle mainSketchStyle;

    private MapSelectOnTouchListener featureSelectOnTouchListener;//要素选择事件
    private View.OnTouchListener defauleOnTouchListener;//默认点击事件

    private boolean isSelect = false;
    private String priPath;

    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {

        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示

        mapView.setSketchEditor(mainSketchEditor);
        mapView.setMagnifierEnabled(true);//放大镜
        mapView.setOnTouchListener(featureSelectOnTouchListener);

    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        //设置widget组件显示内容
        mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_feature_edit,null);

        this.priPath = super.projectPath;

        /** 初始化编辑工具 **/
        mainSketchEditor = new SketchEditor();
        mainSketchStyle = new SketchStyle();
        mainSketchEditor.setSketchStyle(mainSketchStyle);

        defauleOnTouchListener = super.mapView.getOnTouchListener();//默认点击事件

        drawToolsResource = new DrawToolsResource(context,mWidgetView);//初始化编辑工具
        final FeatureTempleteAdapter featureTempleteAdapter = new FeatureTempleteAdapter(context,mapView,mainSketchEditor,drawToolsResource);
        drawToolsResource.gridViewFeatureTemplete.setAdapter(featureTempleteAdapter);

        initAddFeatureTools(featureTempleteAdapter);//初始化要素添加工具
        initEditFeatureTools();//初始化要素编辑工具
    }


    /**
     * 初始化要素添加工具
     * @param featureTempleteAdapter
     */
    private void initAddFeatureTools(final FeatureTempleteAdapter featureTempleteAdapter) {

        drawToolsResource.getFeatureAddTools().txtBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSketchEditor.stop();
                drawToolsResource.getFeatureAddTools().txtSelectLayerName.setText("无");
                drawToolsResource.getFeatureAddTools().toolsView.setVisibility(View.GONE);
                mapView.setMagnifierEnabled(true);//放大镜
            }
        });

        drawToolsResource.getFeatureAddTools().lnrBtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainSketchEditor.canUndo()){
                    mainSketchEditor.undo();
                }else {
                    ToastUtils.showShort(context,"当前状态已经无法回退了");
                }
            }
        });

        drawToolsResource.getFeatureAddTools().lnrBtnGoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainSketchEditor.canRedo()){
                    mainSketchEditor.redo();
                }else{
                    ToastUtils.showShort(context,"当前状态已经无法继续了");
                }
            }
        });

        drawToolsResource.getFeatureAddTools().lnrBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSketchEditor.clearGeometry();
            }
        });

        drawToolsResource.getFeatureAddTools().lnrBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureLayer featureLayer = (FeatureLayer) featureTempleteAdapter.getSelectItem();
                FeatureTable featureTable = featureLayer.getFeatureTable();
                if (featureTable.canAdd()){
                    Feature feature = featureTable.createFeature();
                    Geometry geometry = mainSketchEditor.getGeometry();
                    if (geometry!=null){
                        feature.setGeometry(geometry);
                        ListenableFuture<Void> addFeatureFuture = featureTable.addFeatureAsync(feature);//添加要素
                        addFeatureFuture.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(context,"要素添加成功");
                                mainSketchEditor.clearGeometry();//允许多次添加，这里仅清空

                            }
                        });
                    }else{
                        ToastUtils.showShort(context,"请绘制图形后再试");
                    }
                }else {
                    ToastUtils.showShort(context,"要素添加失败，图层："+featureLayer.getName()+"不支持编辑");
                }
            }
        });
    }

    /**
     * 初始化要素点击工具
     */
    private void initEditFeatureTools() {

        featureSelectOnTouchListener = new MapSelectOnTouchListener(context,mapView,drawToolsResource);

        //退出
        drawToolsResource.getFeatureEditTools().txtBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSketchEditor.stop();
                drawToolsResource.getFeatureEditTools().txtSelectLayerName.setText("无");
                drawToolsResource.getFeatureEditTools().toolsView.setVisibility(View.GONE);

                featureSelectOnTouchListener.clear();//清空当前选择

                drawToolsResource.gridViewFeatureTemplete.setEnabled(true);
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFeature();
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnReDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureLayer featureLayer = featureSelectOnTouchListener.getSelectFeatureLayer();
                GeometryType geometryType = featureLayer.getFeatureTable().getGeometryType();
                switch (geometryType){
                    case POINT:
                    case MULTIPOINT:
                        mainSketchEditor.start(SketchCreationMode.POINT);
                        break;
                    case POLYLINE:
                        mainSketchEditor.start(SketchCreationMode.POLYLINE);
                        break;
                    case POLYGON:
                    case ENVELOPE:
                        mainSketchEditor.start(SketchCreationMode.POLYGON);
                        break;
                    case UNKNOWN:
                        ToastUtils.showShort(context,"当前图层："+ featureLayer.getName()+",数据类型未知");
                        break;
                }
                ToastUtils.showShort(context,"点击屏幕开始重新绘制要素");
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainSketchEditor.canUndo()){
                    mainSketchEditor.undo();
                }else {
                    ToastUtils.showShort(context,"当前状态已经无法回退了");
                }
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureLayer featureLayer = featureSelectOnTouchListener.getSelectFeatureLayer();
                Feature feature = featureSelectOnTouchListener.getSelectFeature();

                FeatureTable featureTable = featureLayer.getFeatureTable();
                if (featureTable.canUpdate(feature)){
                    Geometry geometry = mainSketchEditor.getGeometry();
                    if (geometry!=null){
                        feature.setGeometry(geometry);
                        ListenableFuture<Void> updateFeatureAsync = featureTable.updateFeatureAsync(feature);//添加要素
                        updateFeatureAsync.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(context,"要素编辑成功");
                                mainSketchEditor.clearGeometry();//允许多次添加，这里仅清空

                            }
                        });
                    }else{
                        ToastUtils.showShort(context,"请绘制图形后再试");
                    }

                }else {
                    ToastUtils.showShort(context,"要素编辑失败，图层："+featureLayer.getName()+"不支持编辑");
                }
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnEditAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Feature feature = featureSelectOnTouchListener.getSelectFeature();

                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                View view = LayoutInflater.from(context).inflate( R.layout.widget_view_feature_edit_alert_attribute, null);
                ListView listView = view.findViewById(R.id.widget_view_feature_edit_alert_attribute_lvAttribute);
                FieldAdapter fieldAdapter = new FieldAdapter(context,feature);
                listView.setAdapter(fieldAdapter);

                Button btnOK = view.findViewById(R.id.widget_view_feature_edit_alert_attribute_btnOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FeatureLayer featureLayer = featureSelectOnTouchListener.getSelectFeatureLayer();
                        Feature feature = featureSelectOnTouchListener.getSelectFeature();
                        FeatureTable featureTable = featureLayer.getFeatureTable();
//                        feature.getAttributes().replace("NAME","test");//测试使用
                        if (featureTable.canUpdate(feature)){
                            ListenableFuture<Void> updateFeatureAsync = featureTable.updateFeatureAsync(feature);//添加要素
                            updateFeatureAsync.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showShort(context,"属性保存成功");
                                    mainSketchEditor.clearGeometry();//允许多次添加，这里仅清空
                                }
                            });
                        }else {
                            ToastUtils.showShort(context,"要素编辑失败，图层："+featureLayer.getName()+"不支持编辑");
                        }
                        dialog.dismiss();
                    }
                });

                Button btnCancle = view.findViewById(R.id.widget_view_feature_edit_alert_attribute_btnCancle);
                btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                dialog.setView(view);
                dialog.show();

                if (SysUtils.isPad(context)){
                    //设置弹窗的固定宽高
                    WindowManager m = ((Activity)context).getWindowManager();
                    Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                    WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
                    p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.6
                    p.width = (int) (d.getWidth() * 0.5);    //宽度设置为屏幕的0.6
                    dialog.getWindow().setAttributes(p);
                }
            }
        });

        initMediaTools();
    }

    /**
     * 初始化多媒体工具
     */
    private void initMediaTools() {

        final int RESULT_CAPTURE_IMAGE = 1;// 照相的requestCode
        final int REQUEST_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode
        final int RESULT_CAPTURE_RECORDER_SOUND = 3;// 录音的requestCode

        final String photoPath = priPath+File.separator+"Media/Photo/";
        final String videoPath = priPath+File.separator+"Media/Video/";
        final String audioPath = priPath+File.separator+"Media/Audio/";

        drawToolsResource.getFeatureEditTools().lnrBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feature feature = featureSelectOnTouchListener.getSelectFeature();
                String fileName= feature.getFeatureTable().getTableName()+"_"+String.valueOf(feature.getAttributes().get("FID"))+"_"+ DateUtils.getTimeNow();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File out = new File(photoPath , fileName+".jpg");
                Uri uri = Uri.fromFile(out);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                ((Activity)context).startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feature feature = featureSelectOnTouchListener.getSelectFeature();
                String fileName= feature.getFeatureTable().getTableName()+"_"+String.valueOf(feature.getAttributes().get("FID"))+"_"+ DateUtils.getTimeNow();
                //录视频前必须确认文件夹存在，否则写入异常
                if (FileUtils.isExist(videoPath)==false){
                    FileUtils.createChildFilesDir(videoPath);
                }
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                String videoname =fileName+ ".mp4";
                Uri videoUri = Uri.fromFile(new File(videoPath,videoname));
                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                ((Activity)context).startActivityForResult(videoIntent, REQUEST_CODE_TAKE_VIDEO);
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feature feature = featureSelectOnTouchListener.getSelectFeature();
                String name= feature.getFeatureTable().getTableName()+"_"+String.valueOf(feature.getAttributes().get("FID"))+"_"+ DateUtils.getTimeNow();
                VoiceAlertView baseAlertView = new VoiceAlertView(context,audioPath,name);
                baseAlertView.setTitle("录音");
                baseAlertView.show();
            }
        });

        drawToolsResource.getFeatureEditTools().lnrBtnMediaFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaAlertView alertView = new MediaAlertView(context,photoPath,videoPath,audioPath);
                alertView.show();
            }
        });
    }

    /**
     * 删除要素
     */
    private void deleteFeature() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认删除当前选中要素？");
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
                Feature feature = featureSelectOnTouchListener.getSelectFeature();
                FeatureTable featureTable = featureSelectOnTouchListener.getSelectFeatureLayer().getFeatureTable();
                if (feature!=null){
                    if (featureTable.canDelete(feature)){
                        ListenableFuture<Void> deleteFeaturesAsync =  featureTable.deleteFeatureAsync(feature);
                        deleteFeaturesAsync.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(context,"要素删除成功");
                                drawToolsResource.getFeatureEditTools().toolsView.setVisibility(View.GONE);
                                featureSelectOnTouchListener.clear();//清空当前选择
                            }
                        });
                    }else {
                        ToastUtils.showShort(context,"当前选中要素无法删除");
                    }
                }else {
                    ToastUtils.showShort(context,"当前选中要素为空");
                }
                dialog.dismiss();
            }
        });
        builder.create().show();


    }


    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void inactive(){
        super.inactive();

        returnDefault();

    }

    /**
     * 恢复默认状态
     */
    private void returnDefault() {
        if (defauleOnTouchListener!=null){
            super.mapView.setOnTouchListener(defauleOnTouchListener);//窗口关闭恢复默认点击状态
        }
        featureSelectOnTouchListener.clear();//清空当前选择
        mapView.setMagnifierEnabled(false);//放大镜

        mapView.setSketchEditor(null);
        drawToolsResource.getFeatureAddTools().toolsView.setVisibility(View.GONE);
        drawToolsResource.getFeatureEditTools().toolsView.setVisibility(View.GONE);
    }


}

