package com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer.Config.Entity.WidgetEntity;
import com.gisluq.runtimeviewer.EventBus.BaseWidgetMsgEvent;
import com.gisluq.runtimeviewer.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import gisluq.lib.Util.SysUtils;
import gisluq.lib.Util.ToastUtils;


/**
 * 应用程序组件基类
 * Created by gis-luq on 2018/4/10.
 */

public abstract class BaseWidget {

    public int id = 0;
    public WidgetEntity entity;
    public Context context;
    public String name;
    public MapView mapView;
    public ConfigEntity viewerConfig;
    public String widgetConfig;

//    public View btnWidgetView;//按钮View集合
    public TextView txtWidgetName; //名称
    public ImageView imgWidgetIcon;//图标

    public ImageView imgCenterView;//中心十字叉
    public FloatingActionButton btnCollectPoint;//浮动按钮

    public String projectPath;//工程文件夹路径

    private Callout mCallout;
    private ProgressDialog mProgressDlg;
    private MapView.OnTouchListener mMapOnTouchListener;

    private View widgetContextView;//组件内容视图
    private RelativeLayout widgetExtentView;//组件扩展区域视图

    private boolean isActiveView=false;//当前是否显示
    private boolean isShowCenterView=false;
    private boolean isCollectPointBtn=false;//当前是否显示

    private BaseWidget baseWidget;

    public BaseWidget(){
        baseWidget = this;
        EventBus.getDefault().register(this);//注册事件
    }

    /**
     * 设置扩展区域组件关键
     * @param widgetExtentView
     */
    public void setWidgetExtentView(RelativeLayout widgetExtentView) {
        this.widgetExtentView = widgetExtentView;
    }

    /**
     * 扩展区域显示
     */
    public void removeWidgetExtentView(){
        if (widgetExtentView!=null){
            widgetExtentView.removeAllViews();
        }
    }

    /**
     * 显示扩展区域信息
     * @param view
     */
    public void startWidgetExtentView(View view){
        if (widgetExtentView!=null){
            widgetExtentView.removeAllViews();
            widgetExtentView.addView(view);
        }
    }

    /**
     * 显示加载进度条
     * @param title 标题
     * @param message 消息内容
     */
    public void showLoading(String title, String message) {
        if(mProgressDlg == null)
            mProgressDlg = new ProgressDialog(context);

        mProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDlg.setTitle(title);
        mProgressDlg.setMessage(message);
        if(!mProgressDlg.isShowing()) mProgressDlg.show();
    }

    /**
     * 关闭加载进度条
     */
    public void hideLoading()
    {
        if(mProgressDlg != null) mProgressDlg.dismiss();
    }

    /**
     * 显示消息
     * @param messsage 消息
     */
    public void showMessageBox(String messsage)
    {
        ToastUtils.showShort(context,messsage);
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    public abstract void create();


    /**
     * 组件面板打开时，执行的操作
     * 当点击widget按钮是, WidgetManager将会调用这个方法，面板打开后的代码逻辑.
     * 面板关闭将会调用 "inactive" 方法
     */
    @SuppressLint("ResourceAsColor")
    public void active(){
        //当前面板活动，其他所有面板关闭
        EventBus.getDefault().post(new BaseWidgetMsgEvent(id+"-open"));
        mMapOnTouchListener = (MapView.OnTouchListener) mapView.getOnTouchListener();
        isActiveView =true;

        boolean isPad = SysUtils.isPad(context);
        if (isPad){
            //设置图标样式(仅平板)
            try {
                String name = entity.getSelectIcon();
                if (name!=null){
                    InputStream is = context.getAssets().open(name);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if (imgWidgetIcon!=null){
                        imgWidgetIcon.setImageBitmap(bitmap);
                    }
                }
                if (txtWidgetName!=null){
                    txtWidgetName.setTextColor(context.getColor(R.color.colorPrimaryDark));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    public void inactive(){
        if (mMapOnTouchListener!=null){
            mapView.setOnTouchListener(mMapOnTouchListener);
        }
        isActiveView =false;
        boolean isPad = SysUtils.isPad(context);
        if (isPad){
            //设置图标样式--仅平板
            try {
                String name = entity.getIconName();
                if (name!=null){
                    InputStream is = context.getAssets().open(name);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imgWidgetIcon.setImageBitmap(bitmap);
                }
                txtWidgetName.setTextColor(context.getColor(R.color.deep_gray));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("BaseWidget","inactive, id = "+ this.id);
        }

    }

    /**
     * 获取当前widget是否处于显示状态
     * @return
     */
    public boolean isActiveView(){
        return isActiveView;
    }

    /**
     * 显示Callout信息
     * @param p
     * @param v
     */
    public void showCallout(Point p, View v){
        if(mCallout == null)
            mCallout = mapView.getCallout();
        mCallout.refresh();
        mCallout.show(v,p);
    }

    /**
     * 关闭Callout
     */
    public void hideCallout() {
        if(mCallout!=null) mCallout.dismiss();
    }

    /**
     * 获取插件
     */
    public View getWidgetContextView()
    {
        return widgetContextView;
    }

    /**
     * 显示组件
     * @param v
     */
    public void showWidget(View v)
    {
        widgetContextView = v;
    }

    public void showCenterView(){
        this.isShowCenterView= true;
        this.imgCenterView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取CenterView中心点坐标
     * @return
     */
    public android.graphics.Point getCenterViewPoint(){
        android.graphics.Point point = null;

        //获取imgCenterView父容器
        View view = imgCenterView.getRootView();
        int x = view.getWidth() - imgCenterView.getRight()+imgCenterView.getWidth()/2;
        int y = imgCenterView.getTop()+imgCenterView.getHeight()/2;

        point = new android.graphics.Point(x,y);
        return point;
    }

    public void hideCenterView(){
        this.isShowCenterView =false;
        this.imgCenterView.setVisibility(View.GONE);
    }

    /**
     * 中心十字叉是否显示
     * @return
     */
    public boolean isShowCenterView() {
        return isShowCenterView;
    }

    /**
     * 显示浮动按钮、用于精确采集点
     */
    public void showCollectPointBtn(){
        this.isCollectPointBtn = true;
        this.btnCollectPoint.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏浮动按钮
     */
    public void hideCollectPointBtn(){
        this.isCollectPointBtn = false;
        this.btnCollectPoint.setVisibility(View.GONE);
    }

    /**
     * 采集按钮是否显示
     * @return
     */
    public boolean isCollectPointBtn() {
        return isCollectPointBtn;
    }

    /**
     * 设置浮动按钮点击事件
     * @param onClickListener
     */
    public void setCollectPointListener(View.OnClickListener onClickListener){
        this.btnCollectPoint.setOnClickListener(onClickListener);
    }

    /**
     * 关闭组件
     */
    public void hideWidget() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100)//优先级100
    public void onMoonEvent(BaseWidgetMsgEvent baseWidgetMsgEvent){
        if(baseWidgetMsgEvent.getMessage().equals(id+"-open")){
           //判断当前页面是否活动，如果活动不执行任何操作
        }else{
           //关闭所有非活动状态
           Method method = null; // 父类对象调用子类方法(反射原理)
           try {
               method = baseWidget.getClass().getMethod("inactive");
               Object o = method.invoke(baseWidget);
           } catch (NoSuchMethodException e) {
               e.printStackTrace();
           } catch (IllegalAccessException e) {
               e.printStackTrace();
           } catch (InvocationTargetException e) {
               e.printStackTrace();
           }
       }
    }

    /**
     * 设置widget组件按钮view
     * @param textView
     * @param imageView
     */
    public void setWidgetBtnView(TextView textView, ImageView imageView) {
        this.txtWidgetName  = textView;
        this.imgWidgetIcon =  imageView;
    }

}
