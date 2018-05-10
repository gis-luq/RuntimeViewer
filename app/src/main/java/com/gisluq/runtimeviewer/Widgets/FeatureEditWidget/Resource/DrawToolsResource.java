package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Resource;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gisluq.runtimeviewer.R;

import java.util.zip.CheckedOutputStream;

/**
 * 要素绘制工具UI信息
 * Created by gis-luq on 2018/4/20.
 */
public class DrawToolsResource {

    private Context context;
    private View view;

    public GridView gridViewFeatureTemplete;
    private FeatureAddTools featureAddTools;
    private FeatureEditTools featureEditTools;

    public DrawToolsResource(Context context, View v){
        this.context = context;
        this.view = v;
        initResource();
    }

    /**
     * 初始化UI资源
     */
    private void initResource() {

        gridViewFeatureTemplete = view.findViewById(R.id.widget_view_feature_edit_feaTempleteGridView);

        featureAddTools = new FeatureAddTools();
        featureEditTools = new FeatureEditTools();

        featureAddTools.toolsView = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd);
        featureAddTools.txtBtnExit = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_txtBtnExit);
        featureAddTools.txtSelectLayerName = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_txtSelectLayer);
        featureAddTools.lnrBtnGoBack = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_linerBtnGoBack);
        featureAddTools.lnrBtnGoOn = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_linerBtnGoOn);
        featureAddTools.lnrBtnClear = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_linerBtnClear);
        featureAddTools.lnrBtnEditAttribute = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_linerBtnEditAttribute);
        featureAddTools.lnrBtnSave = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewAdd_linerBtnSave);


        featureEditTools.toolsView = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit);
        featureEditTools.txtBtnExit = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_txtBtnExit);
        featureEditTools.txtSelectLayerName = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_txtSelectLayer);
        featureEditTools.lnrBtnDelete = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnDelete);
        featureEditTools.lnrBtnReDraw = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnReDraw);
        featureEditTools.lnrBtnGoBack = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnGoBack);
        featureEditTools.lnrBtnEditAttribute = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnEditAttribute);
        featureEditTools.lnrBtnSave = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnSave);

        featureEditTools.lnrBtnCamera = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnCamera);
        featureEditTools.lnrBtnVideo = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnVideo);
        featureEditTools.lnrBtnVoice = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnVoice);
        featureEditTools.lnrBtnMediaFiles = view.findViewById(R.id.widget_view_feature_edit_item_toolsViewEdit_linerBtnMediaFiles);
    }

    /**
     * 获取添加工具
     * @return
     */
    public FeatureAddTools getFeatureAddTools() {
        return featureAddTools;
    }

    /**
     * 获取编辑工具
     * @return
     */
    public FeatureEditTools getFeatureEditTools() {
        return featureEditTools;
    }

    /**
     * 要素添加工具
     */
    public class FeatureAddTools{
        public View toolsView;
        public TextView txtBtnExit;//退出
        public TextView txtSelectLayerName;//当前选中图层
        public LinearLayout lnrBtnGoBack;//回退
        public LinearLayout lnrBtnGoOn;//回退
        public LinearLayout lnrBtnClear;//清除
        public LinearLayout lnrBtnEditAttribute;//属性
        public LinearLayout lnrBtnSave;//保存
    }

    /**
     * 要素编辑工具
     */
    public class FeatureEditTools{
        public View toolsView;
        public TextView txtBtnExit;//退出
        public TextView txtSelectLayerName;//当前选中图层
        public LinearLayout lnrBtnDelete;//删除
        public LinearLayout lnrBtnReDraw;//重绘
        public LinearLayout lnrBtnGoBack;//回退
        public LinearLayout lnrBtnEditAttribute;//属性
        public LinearLayout lnrBtnSave;//保存

        public LinearLayout lnrBtnCamera;//拍照
        public LinearLayout lnrBtnVideo;//录屏
        public LinearLayout lnrBtnVoice;//录音
        public LinearLayout lnrBtnMediaFiles;//文件管理
    }
}
