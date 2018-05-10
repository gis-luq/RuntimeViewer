package com.gisluq.runtimeviewer.Widgets.CalculateWidget;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.gisluq.runtimeviewer.R;

import gisluq.lib.Util.ToastUtils;

/**
 * 测量组件
 * Created by gis-luq on 2018/3/10.
 */
public class CaculateWidget extends BaseWidget {

    public View mWidgetView = null;//
    private TextView txtResult =null;//量算统计结果

    private SketchEditor mainSketchEditor;
    private SketchStyle mainSketchStyle;

    private Handler mHandler ;

    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void active() {

        super.active();//默认需要调用，以保证切换到其他widget时，本widget可以正确执行inactive()方法并关闭
        super.showWidget(mWidgetView);//加载UI并显示

//        super.showMessageBox(super.name);//显示组件名称
        mapView.setSketchEditor(mainSketchEditor);

    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        //设置widget组件显示内容
        mWidgetView = mLayoutInflater.inflate(R.layout.widget_view_calculate,null);

        initView();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                txtResult.setText(msg.obj.toString());
            }
        };

        /** 初始化编辑工具 **/
        mainSketchEditor = new SketchEditor();
        mainSketchStyle = new SketchStyle();
        mainSketchEditor.setSketchStyle(mainSketchStyle);
        mainSketchEditor.addGeometryChangedListener(new SketchGeometryChangedListener() {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent) {
                Geometry geometry = sketchGeometryChangedEvent.getGeometry();
                if (geometry==null) return;
                GeometryType type = geometry.getGeometryType();
                Message message = Message.obtain(mHandler);//利用Handle操作UI线程
                switch (type){
                    case POINT:
                        Point point = (Point) GeometryEngine.project(geometry, SpatialReference.create(4326));
                        String point_str = "经度："+point.getX()+"\n纬度："+point.getY();
//                        txtResult.setText(point_str);
                        message.obj = point_str;
                        mHandler.sendMessage(message);
                        break;
                    case POLYLINE:
                        double length = GeometryEngine.length((Polyline) geometry);
                        String length_str = "长度："+getLengthString(length);
//                        txtResult.setText(length_str);
                        message.obj = length_str;
                        mHandler.sendMessage(message);
                        break;
                    case POLYGON:
                        double area = GeometryEngine.area((Polygon)geometry);
                        String area_str = "面积："+getAreaString(area);
//                        txtResult.setText(area_str);
                        message.obj = area_str;
                        mHandler.sendMessage(message);
                        break;
                }
            }
        });

    }

    /**
     * 初始化系统UI
     */
    private void initView() {
        txtResult = mWidgetView.findViewById(R.id.widget_view_calculate_txtResult);
        LinearLayout lnbtnLocation = mWidgetView.findViewById(R.id.widget_view_calculate_linerBtnGetLocation);
        LinearLayout lnbtnLength = mWidgetView.findViewById(R.id.widget_view_calculate_linerBtnGetLength);
        LinearLayout lnbtnArea = mWidgetView.findViewById(R.id.widget_view_calculate_linerBtnGetArea);

        TextView txtBtnClear = mWidgetView.findViewById(R.id.widget_view_calculate_txtBtnClear);
        txtBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSketchEditor.clearGeometry();
                txtResult.setText("空");
            }
        });

        lnbtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(context,"量算开始，点击屏幕获取坐标信息");
                mainSketchEditor.clearGeometry();
                mainSketchEditor.start(SketchCreationMode.POINT);
            }
        });

        lnbtnLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(context,"量算开始，点击屏幕绘制线，获取长度信息");
                mainSketchEditor.clearGeometry();
                mainSketchEditor.start(SketchCreationMode.POLYLINE);
            }
        });

        lnbtnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(context,"量算开始，点击屏幕绘制面，获取面积信息");
                mainSketchEditor.clearGeometry();
                mainSketchEditor.start(SketchCreationMode.POLYGON);
            }
        });

    }

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    @Override
    public void inactive(){
        super.inactive();
        mapView.setSketchEditor(null);
        mainSketchEditor.stop();
    }

    /**
     * 获取长度信息
     * @param dValue
     * @return
     */
    private String getLengthString(double dValue){
        long length = Math.abs(Math.round(dValue));
        String sLength = "";
        if (length>1000){
            double dArea = length / 1000.0;
            sLength = Double.toString(dArea) + "千米";//平方公里
        }else {
            sLength = Double.toString(length) + "米";//平方米
        }
        return sLength;
    }

    /**
     * 获取面积信息
     * @param dValue
     * @return
     */
    private String getAreaString(double dValue){
        long area = Math.abs(Math.round(dValue));
        String sArea = "";
        // 顺时针绘制多边形，面积为正，逆时针绘制，则面积为负
        if(area >= 1000000){
            double dArea = area / 1000000.0;
            sArea = Double.toString(dArea) + "平方千米";//平方公里
        } else
            sArea = Double.toString(area) + "平方米";//平方米
        return sArea;
    }

}

