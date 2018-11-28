package com.gisluq.runtimeviewer.BMOD.MapModule.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.FeatureLayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.WidgetManager;
import com.gisluq.runtimeviewer.Config.AppConfig;
import com.gisluq.runtimeviewer.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer.Config.Entity.WidgetEntity;
import com.gisluq.runtimeviewer.BMOD.MapModule.Map.MapManager;
import com.gisluq.runtimeviewer.BMOD.MapModule.Resource.ResourceConfig;
import com.gisluq.runtimeviewer.Base.BaseActivity;
import com.gisluq.runtimeviewer.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gisluq.lib.Util.SysUtils;
import gisluq.lib.Util.ToastUtils;

public class MapActivity extends BaseActivity {

    private Context context;
    private ResourceConfig resourceConfig;//UI资源绑定

    private ConfigEntity mConfigEntity;//应用程序配置信息
    private MapManager mMapManager;//地图管理器
    private WidgetManager mWidgetManager;//组件管理器

    private Map<Integer,Object> mWidgetEntityMenu = new HashMap<>();//Widget Menu列表信息

    private TextView titleTextView;//工程标题
    private String DirName;//工程名称
    private String DirPath;//工程路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        context = this;

        Intent intent = getIntent();
        DirName = intent.getStringExtra("DirName");
        DirPath = intent.getStringExtra("DirPath");
        titleTextView.setText(DirName);//显示工程文件夹名称

        resourceConfig = new ResourceConfig(context);//初始化应用程序资源列表
        init();//初始化应用程序
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 横竖屏切换时--保证应用程序不刷新重置
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    protected void onResume() {
        /** 手机强制设置为纵屏*/
        boolean ispad = SysUtils.isPad(context);
        if (ispad==false){
            if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        super.onResume();
    }

    /**
     * 初始化该地图和应用程序组件
     */
    private void init() {
        //读取应用程序配置信息
        mConfigEntity = AppConfig.getConfig(context);
        //初始化底图组件信息-利用配置文件
        mMapManager = new MapManager(context, resourceConfig, mConfigEntity,DirPath);

        //初始化应用程序组件
        mWidgetManager = new WidgetManager(context, resourceConfig, mMapManager, mConfigEntity,DirPath);
        //实例化Widget功能模块
        mWidgetManager.instanceAllClass();

    }

    /***
     * 初始化应用程序状态栏显示
     * @param toolbar
     */
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
//        toolbar.setNavigationIcon(null);//设置不显示回退按钮
        getLayoutInflater().inflate(R.layout.activity_toobar_view, toolbar);
        titleTextView = (TextView) toolbar.findViewById(R.id.activity_baseview_toobar_view_txtTitle);
        titleTextView.setTextSize(18);
        titleTextView.setPadding(0, 0, 0, 0);
    }

    /**
     * 根据配置文件初始化系统功能菜单栏
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        boolean isPad = SysUtils.isPad(context);
        if (isPad){
            /***
             * 方案二模式，平板左侧菜单
             */
            //根据配置文件初始化系统功能菜单栏
            if (mConfigEntity != null) {
                final List<WidgetEntity> mListWidget = mConfigEntity.getListWidget();

                for (int i = 0; i < mListWidget.size(); i++) {
                    final WidgetEntity widgetEntity = mListWidget.get(i);

                    //widget按钮初始化操作
                    View view = LayoutInflater.from(context).inflate(R.layout.base_widget_view_tools_widget_btn, null);
                    final LinearLayout ltbtn = view.findViewById(R.id.base_widget_view_tools_widget_btn_lnbtnWidget);
                    TextView textViewName = (TextView) view.findViewById(R.id.base_widget_view_tools_widget_btn_txtWidgetToolName);
                    ImageView imageView = (ImageView)view.findViewById(R.id.base_widget_view_tools_widget_btn_imgWidgetToolIcon);
                    //设置按钮对应UI
                    mWidgetManager.setWidgetBtnView(widgetEntity.getId(),textViewName,imageView);

                    ltbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = widgetEntity.getId();
                            BaseWidget widget = mWidgetManager.getSelectWidget();//当前选中
                            if (widget!=null){
                                //当前有选中的widget
                                if (id == widget.id){
                                    //判断当前是否显示状态
                                    if (mWidgetManager.getSelectWidget().isActiveView()){
                                        mWidgetManager.startWidgetByID(id);//显示widget
                                    }else {
                                        mWidgetManager.hideSelectWidget();
                                    }

                                }else {
                                    mWidgetManager.startWidgetByID(id);//显示widget
                                }
                            }else {
                                //当前未选中widget
                                mWidgetManager.startWidgetByID(id);//显示widget
                            }

                        }
                    });
                    textViewName.setText(widgetEntity.getLabel());

                    try {
                        String name = widgetEntity.getIconName();
                        if (name!=null){
                            InputStream is = getAssets().open(name);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    resourceConfig.baseWidgetToolsView.addView(view);

                }
            }
        }else {
            /***
             * 方案一模式，采用右侧顶部显示菜单--手机模式
             */
            //根据配置文件初始化系统功能菜单栏
            if (mConfigEntity != null) {
                List<WidgetEntity> mListWidget = mConfigEntity.getListWidget();

                String widgetGroup = "";
                List<WidgetEntity> mGroupListWidget = new ArrayList<>();

                for (int i = 0; i < mListWidget.size(); i++) {
                    WidgetEntity widgetEntity = mListWidget.get(i);
                    if(widgetEntity.getGroup()==""){
                        MenuItem menuItem= menu.add(Menu.NONE, Menu.FIRST + i, 0, widgetEntity.getLabel());
                        //记录菜单ID和WidgetEntity间对应关系
                        mWidgetEntityMenu.put(menuItem.getItemId(),widgetEntity);
                        if (widgetEntity.getIsShowing()) {
                            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//显示到状态栏
                        }
                    }else {
                        if (widgetGroup == "") {
                            widgetGroup = widgetEntity.getGroup();//设置组名称
                            //获取组名
                            MenuItem menuItem = menu.add(Menu.NONE, Menu.FIRST + i, 0, widgetGroup);
                            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//显示到状态栏
                            //记录菜单ID和WidgetEntity间对应关系
                            mWidgetEntityMenu.put(menuItem.getItemId(),mGroupListWidget);
                        }
                        if (widgetEntity.getGroup().equals(widgetGroup)) {//同一个组
                            mGroupListWidget.add(widgetEntity);//记录widget组信息
                        } else {
                            //不同组
                        }
                    }

                }
            }

        }
        return true;
    }

    /**
     * 设置应用程序菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            exitActivity();
            //如果这里不加上该句代码，一点击到后退键该界面就消失了，选择框就一闪掉了，选择不了
            return true;
        }

        super.onOptionsItemSelected(item);
        Object object =  mWidgetEntityMenu.get(item.getItemId());
        if (object!=null){
            if (object.getClass().equals(WidgetEntity.class)){
                WidgetEntity widgetEntity = (WidgetEntity)object;
                if (widgetEntity!=null){
                    mWidgetManager.startWidgetByID(widgetEntity.getId());//显示widget
                }else {
                    ToastUtils.showShort(context,"WidgetEntity为空");
                }
            }else if (object.getClass().equals(ArrayList.class)){
                final List<WidgetEntity> list = (List<WidgetEntity>)object;
                if (list!=null){
                    PopupMenu pmp = new PopupMenu(context,findViewById(item.getItemId()));
                    for (int i=0;i<list.size();i++){
                        WidgetEntity entity = list.get(i);
                        pmp.getMenu().add(entity.getLabel());
                    }
                    pmp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //子菜单点击事件
                            for (int i=0;i<list.size();i++){
                                WidgetEntity entity = list.get(i);
                                if (entity.getLabel().equals(item.getTitle())){
                                    mWidgetManager.startWidgetByID(entity.getId());//显示widget
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    pmp.show();
                }
            }
        }

        return true;
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
        builder.setMessage("是否退出当前工程？");
        builder.setTitle("系统提示");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		//预留处理拍照后回调问题
        //EventBus.getDefault().post(new MessageEvent("camera-"+requestCode,0));
    }


}
