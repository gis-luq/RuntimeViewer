package com.gisluq.runtimeviewer.Widgets;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;

import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.gisluq.runtimeviewer.R;

/**
 * 测试用组件
 * Created by gis-luq on 2018/5/10.
 */
public class HelloWorldWidget extends BaseWidget {

    public View mWidgetView = null;//
    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {

        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示

        super.showMessageBox(super.name);//显示组件名称

        super.mapView.getMap().getBasemap().getBaseLayers();
        super.mapView.getMap().getOperationalLayers();
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        //设置widget组件显示内容
        mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_helloworld,null);

    }

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void inactive(){
        super.inactive();
    }

}

