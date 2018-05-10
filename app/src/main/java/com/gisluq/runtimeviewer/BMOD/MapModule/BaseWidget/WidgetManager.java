package com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer.Config.Entity.WidgetEntity;

import com.gisluq.runtimeviewer.BMOD.MapModule.Map.MapManager;
import com.gisluq.runtimeviewer.BMOD.MapModule.Resource.ResourceConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gisluq.lib.Util.ToastUtils;

/**
 * 应用程序组件管理器
 * Created by gis-luq on 2018/4/10.
 */

public class WidgetManager {

    private Context context;
    private ResourceConfig resourceConfig;
    private MapManager mMapManager;
    private ConfigEntity mConfigEntity;

    private List<WidgetEntity> mListWidget;
    private static Map<Integer,BaseWidget> mInstanceWidget = new HashMap<>();//实例化的Widget列表信息

    private BaseWidgetControl baseWidgetControl = null;//组件窗体控件

    private int selectWidgetID = -1;//当前选中的组件ID

    private String projectPath=null;//工程目录空间

    public WidgetManager(Context context, ResourceConfig resourceConfig, MapManager mMapManager, ConfigEntity mConfigEntity, String dirPath) {
        this.context = context;
        this.resourceConfig = resourceConfig;
        this.mMapManager =mMapManager;
        this.mConfigEntity = mConfigEntity;

        this.projectPath = dirPath;

        this.mListWidget = mConfigEntity.getListWidget();//组件列表

        baseWidgetControl = new BaseWidgetControl(context);

    }


    /**
     * 实例化组件类
     */
    public void instanceAllClass() {
        Class<?> cc;
        int len = mListWidget.size();
        if(len == 0) return;
        for(int i=0;i<len;i++)
        {
            try {
                cc = Class.forName(mListWidget.get(i).getClassname());
                BaseWidget widget = (BaseWidget)cc.newInstance();
                Log.d("","id="+mListWidget.get(i).getId()+","+mListWidget.get(i).getClassname());
                instanceWidget(widget, mListWidget.get(i));
                widget.create();
                mInstanceWidget.put(mListWidget.get(i).getId(), widget);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort(context,mListWidget.get(i).getLabel()+"组件加载失败:"+e.getMessage());
            }
        }
    }

    /**\
     * 组件封装
     * @param widget
     * @param entity
     */
    private void instanceWidget(BaseWidget widget, WidgetEntity entity) {
        widget.context = context;
        widget.id = entity.getId();
        widget.mapView = resourceConfig.mapView;
        widget.imgCenterView = resourceConfig.imgCenterView;
        widget.viewerConfig = mConfigEntity;
        widget.name = entity.getLabel();

        //默认imgCenterView不显示
        widget.imgCenterView.setVisibility(View.GONE);

        widget.projectPath = this.projectPath;

        widget.widgetConfig = "";
        //判断widget是否有对应的配置文件
        if(!entity.getConfig().equals(""))
        {
            InputStream is = widget.getClass().getResourceAsStream(entity.getConfig());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null, div = "";
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(div + line);
                    div = "\n";
                }
                widget.widgetConfig  = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 打开组件
     * @param widgetId 组件ID
     */
    public void startWidgetByID(int widgetId) {

        if (selectWidgetID!=widgetId){//判断是否是当前组件
            BaseWidget widget = mInstanceWidget.get(widgetId);

            /**
             * 需要严格按照以下顺序执行
             * 1、setWidget()
             * 2、startBaseWiget()
             * 3、baseWigetViewContext.removeAllViews()
             * 4、baseWigetViewContext.addView(v)
             */
            if(widget!=null){
                baseWidgetControl.setTitle(widget.name);
                baseWidgetControl.setWidget(widget);
                baseWidgetControl.startBaseWiget();

                baseWidgetControl.baseWigetViewContext.removeAllViews();
                View v = widget.getWidgetContextView();
                if (v!=null){
                    baseWidgetControl.baseWigetViewContext.addView(v);
                }

                selectWidgetID = widgetId;
            }else{
                ToastUtils.showLong(context,"组件打开失败，请请检查配置信息是否正确");
            }
        }else {
            //显示当前widget
            baseWidgetControl.startBaseWiget();
        }
    }

    /**
     * 获取当前选中widget
     * @return
     */
    public BaseWidget getSelectWidget(){
        BaseWidget widget = null;
        if (selectWidgetID!=-1){
            widget = mInstanceWidget.get(selectWidgetID);
        }
       return widget;
    }

    /**
     * 关闭组件
     */
    public void hideSelectWidget(){
        baseWidgetControl.hideWidget();
        selectWidgetID=-1;
    }

}
