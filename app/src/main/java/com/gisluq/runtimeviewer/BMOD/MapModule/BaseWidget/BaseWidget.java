package com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer.EventBus.BaseWidgetMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import gisluq.lib.Util.ToastUtils;


/**
 * 应用程序组件基类
 * Created by gis-luq on 2018/4/10.
 */

public abstract class BaseWidget {

    public int id = 0;
    public Context context;
    public String name;
    public MapView mapView;
    public ConfigEntity viewerConfig;
    public String widgetConfig;

    public ImageView imgCenterView;//中心十字叉

    public String projectPath;//工程文件夹路径

    private Callout mCallout;
    private ProgressDialog mProgressDlg;
    private MapView.OnTouchListener mMapOnTouchListener;

    private View widgetContextView;//组件内容视图

    public boolean isActiveView=false;//当前是否显示

    private BaseWidget baseWidget;

    public BaseWidget(){
        baseWidget = this;
        EventBus.getDefault().register(this);//注册事件
    }

    /**
     * 显示加载进度条
     * @param title 标题
     * @param message 消息内容
     */
    public void showLoading(String title, String message)
    {
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
    public void active(){
        //当前面板活动，其他所有面板关闭
        EventBus.getDefault().post(new BaseWidgetMsgEvent(id+"-open"));
        mMapOnTouchListener = (MapView.OnTouchListener) mapView.getOnTouchListener();
        isActiveView =true;
    };

    /**
     * 组件面板关闭时，执行的操作
     * 面板关闭将会调用 "inactive" 方法
     */
    public void inactive(){
        if (mMapOnTouchListener!=null){
            mapView.setOnTouchListener(mMapOnTouchListener);
        }
        isActiveView =false;
        Log.d("BaseWidget","inactive, id = "+ this.id);
    };

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
    public void showCallout(Point p, View v)
    {
        if(mCallout == null)
            mCallout = mapView.getCallout();
        mCallout.refresh();
        mCallout.show(v,p);
    }

    /**
     * 关闭Callout
     */
    public void hideCallout()
    {
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
        this.imgCenterView.setVisibility(View.VISIBLE);
    }

    public void hideCenterView(){
        this.imgCenterView.setVisibility(View.GONE);
    }

    /**
     * 关闭组件
     */
    public void hideWidget()
    {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
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



}
