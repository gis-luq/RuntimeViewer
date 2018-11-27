package com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gisluq.runtimeviewer.R;

/**
 * 应用程序组件基类
 * 负责和UI交互
 * Created by gis-luq on 2018/4/10.
 */

public class BaseWidgetControl {

    /**上下文，创建view的时候需要用到*/
    private Context context;

    /**视图-全部*/
    private View baseWigetView;

    /**组件视图-不包括打开按钮*/
    private View wigetView;

    /**标题*/
    private TextView txtTitleView;

    /**从内存关闭组件*/
    private Button btnRemoveWidget;

    /**打开组件*/
    private Button btnOpenWidget;

    /**零时关闭组件*/
    private Button btnClosedWidget;

    /**内容区域*/
    public RelativeLayout baseWigetViewContext;

    /**组件扩展区域*/
    public RelativeLayout baseWidgetExtentView;

    private BaseWidget widget;//组件信息

    public BaseWidgetControl(Context context){
        this.context = context;
        initBaseWidgetView();
        baseWigetView.setVisibility(View.GONE);//默认状态

    }

    public void setTitle( String title){
        txtTitleView.setText(title);
    }

    /**
     * 显示组件视图
     * @param v
     */
    public void startBaseWiget(View v){

        ViewParent viewParent = v.getParent();
        if (viewParent!=null){
            RelativeLayout vp = (RelativeLayout) viewParent;
            vp.removeAllViews();
        }

        this.baseWigetViewContext.removeAllViews();
        this.baseWigetViewContext.addView(v);

        baseWigetView.setVisibility(View.VISIBLE);//Widget主框架-显示
        wigetView.setVisibility(View.VISIBLE);//Widget视图-显示
        btnOpenWidget.setVisibility(View.GONE);//打开按钮-默认不显示

        animOpen();
    }


    /**
     * 初始化组件视图
     */
    private void initBaseWidgetView() {

        baseWigetView = ((Activity)context).findViewById(R.id.base_widget_view_baseview);
        wigetView = ((Activity)context).findViewById(R.id.base_widget_view_widgetview);

        txtTitleView = (TextView) baseWigetView.findViewById(R.id.base_widget_view_txtTitle);
        btnRemoveWidget = (Button) baseWigetView.findViewById(R.id.base_widget_view_btnRemove);
        btnOpenWidget = (Button) baseWigetView.findViewById(R.id.base_widget_view_btnOpen);
        btnClosedWidget = (Button) baseWigetView.findViewById(R.id.base_widget_view_btnClosed);
        baseWigetViewContext = (RelativeLayout) baseWigetView.findViewById(R.id.base_widget_view_ContextView);
        baseWidgetExtentView = (RelativeLayout) baseWigetView.findViewById(R.id.base_widget_view_widgetExtendview);

        btnOpenWidget.setVisibility(View.GONE);//默认不显示

        //关闭widget
        btnRemoveWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                widget.inactive();//widget处于关闭状态
                baseWigetView.setVisibility(View.GONE);

                animClosed();
            }
        });

        //打开widget
        btnOpenWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wigetView.setVisibility(View.VISIBLE);
                btnOpenWidget.setVisibility(View.GONE);

                animOpen();
            }
        });

        //不显示widget
        btnClosedWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //仅仅是不显示，但是出于活动状态
                wigetView.setVisibility(View.GONE);
                btnOpenWidget.setVisibility(View.VISIBLE);

                animClosed();
            }
        });
    }

    /**
     * 设置组件信息
     * @param widget
     */
    public void setWidget(BaseWidget widget) {
        this.widget = widget;
        this.widget.setWidgetExtentView(baseWidgetExtentView);
        this.widget.active();//打开组件时执行active方法用于装载UI信息
    }

    /**
     * 关闭组件
     */
    public void hideWidget(){
        widget.inactive();//widget处于关闭状态
        baseWigetView.setVisibility(View.GONE);

        animClosed();
    }

    /**
     * 打开动画
     */
    private void animOpen() {
//        Animation animationOpen = AnimationUtils.loadAnimation(context,R.anim.widget_enter_left);
//        baseWigetView.startAnimation(animationOpen);
    }

    /**
     * 关闭动画
     */
    private void animClosed() {
//        Animation animationExit = AnimationUtils.loadAnimation(context, tech.esricd.unspacecollector.R.anim.widget_exit_left);
//        baseWigetView.startAnimation(animationExit);
    }



}
