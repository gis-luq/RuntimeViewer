package com.gisluq.runtimeviewer.Widgets.QueryWidget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.gisluq.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Adapter.LayerSpinnerAdapter;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Adapter.QueryResultAdapter;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Listener.MapQueryOnTouchListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import gisluq.lib.Util.ToastUtils;

/**
 * 属性查询组件-图查属性，属性查图
 * Created by gis-luq on 2018/3/10.
 */
public class QueryWidget extends BaseWidget {

    private static String TAG = "QueryWidget";
    private View.OnTouchListener defauleOnTouchListener;//默认点击事件
    private MapQueryOnTouchListener mapQueryOnTouchListener;//要素选择事件

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
        initMapQuery();
    }

    /**
     * widget组件的初始化操作，包括设置view内容，逻辑等
     * 该方法在应用程序加载完成后执行
     */
    @Override
    public void create() {
        this.context = context;
        defauleOnTouchListener = super.mapView.getOnTouchListener();
        initWidgetView();//初始化UI
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
     * 初始化UI
     */
    private void initWidgetView() {
        /**
         * **********************************************************************************
         * 布局容器
         */
        //设置widget组件显示内容
        mWidgetView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query,null);
        final TextView txtMapQueryBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_query_txtBtnMapQuery);
        final View viewMapQuerySelect = mWidgetView.findViewById(R.id.widget_view_query_viewMapQuery);
        TextView txtAttributeQueryBtn = (TextView)mWidgetView.findViewById(R.id.widget_view_query_txtBtnAttributeQuery);
        final View viewAttributeSelect = mWidgetView.findViewById(R.id.widget_view_query_viewAttributeQuery);
        final RelativeLayout viewContent = mWidgetView.findViewById(R.id.widget_view_query_contentView);//内容区域

        /**
         * **********************************************************************************
         * 图查属性
         */
        final View mapQueryView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query_mapquery,null);
//        View viewBtnSelectFeature = mapQueryView.findViewById(R.id.widget_view_query_mapquery_linerBtnFeatureSelect);//要素选择
        TextView txtLayerName = (TextView)mapQueryView.findViewById(R.id.widget_view_query_mapquery_txtLayerName);
        ListView listViewField = (ListView)mapQueryView.findViewById(R.id.widget_view_query_mapquery_fieldListview);
        mapQueryOnTouchListener = new MapQueryOnTouchListener(context,mapView,txtLayerName,listViewField);

        /**
         * **********************************************************************************
         * 属性查图
         */
        final View attributeQueryView = LayoutInflater.from(super.context).inflate(R.layout.widget_view_query_attributequery,null);
        final Spinner spinnerLayerList = (Spinner)attributeQueryView.findViewById(R.id.widget_view_query_attributequery_spinnerLayer);
        final TextView txtQueryInfo = (TextView)attributeQueryView.findViewById(R.id.widget_view_query_attributequery_txtQueryInfo);
        Button btnQuery = (Button)attributeQueryView.findViewById(R.id.widget_view_query_attributequery_btnQuery);
        final ListView resultListview = (ListView)attributeQueryView.findViewById(R.id.widget_view_query_attributequery_resultListview);

        LayerSpinnerAdapter layerSpinnerAdapter = new LayerSpinnerAdapter(context,mapView.getMap().getOperationalLayers());
        spinnerLayerList.setAdapter(layerSpinnerAdapter);

        //属性查图
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取查询图层
                Object obj= spinnerLayerList.getSelectedItem();
                if (obj!=null){
                    final FeatureLayer featureLayer = (FeatureLayer)obj;
                    //获取模糊查询关键字
                    String search= txtQueryInfo.getText().toString();
                    queryAttrubute(featureLayer, search, resultListview);
                }
            }
        });


        /**
         * **********************************************************************************
         * 布局容器事件
         */
        viewContent.addView(mapQueryView);//默认显示图层列表
        txtMapQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewContent.getChildAt(0)!=mapQueryView){
                    viewContent.removeAllViews();
                    viewContent.addView(mapQueryView);
                    viewMapQuerySelect.setVisibility(View.VISIBLE);
                    viewAttributeSelect.setVisibility(View.GONE);
                    initMapQuery();//初始化属性查图
                }
            }
        });
        txtAttributeQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewContent.getChildAt(0)!=attributeQueryView){
                    viewContent.removeAllViews();
                    viewContent.addView(attributeQueryView);
                    viewMapQuerySelect.setVisibility(View.GONE);
                    viewAttributeSelect.setVisibility(View.VISIBLE);
                    returnDefault();//还原默认状态
                }
            }
        });

    }

    /**
     *  属性查询
     * @param featureLayer
     * @param search
     * @param resultListview
     */
    private void queryAttrubute(FeatureLayer featureLayer, String search, final ListView resultListview) {
        final FeatureLayer mainFeatureLayer = featureLayer;
        mainFeatureLayer.setSelectionWidth(15);
        mainFeatureLayer.setSelectionColor(Color.YELLOW);

        QueryParameters query = new QueryParameters();
        String whereStr = GetWhereStrFunction(featureLayer,search);
        query.setWhereClause(whereStr);
        final ListenableFuture<FeatureQueryResult> featureQueryResult
                = featureLayer.getFeatureTable().queryFeaturesAsync(query);
        featureQueryResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {

                    List<Feature> mapQueryResult = new ArrayList<>();//查询统计结果

                    FeatureQueryResult result = featureQueryResult.get();
                    Iterator<Feature> iterator = result.iterator();
                    Feature feature;
                    while (iterator.hasNext()) {
                        feature = iterator.next();
                        mapQueryResult.add(feature);
                    }

                    ToastUtils.showShort(context,"查询出"+mapQueryResult.size()+"个符合要求的结果");
                    QueryResultAdapter queryResultAdapter = new QueryResultAdapter(context,mapQueryResult,mapView);
                    resultListview.setAdapter(queryResultAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取模糊查询字符串
     * @param featureLayer
     * @param search
     * @return
     */
    private String GetWhereStrFunction(FeatureLayer featureLayer, String search) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Field> fields = featureLayer.getFeatureTable().getFields();
        boolean isNumber = isNumberFunction(search);
        for (Field field : fields) {
            switch (field.getFieldType()) {
                case TEXT:
                    stringBuilder.append(" upper(");
                    stringBuilder.append(field.getName());
                    stringBuilder.append(") LIKE '%");
                    stringBuilder.append(search.toUpperCase());
                    stringBuilder.append("%' or");
                    break;
                case SHORT:
                case INTEGER:
                case FLOAT:
                case DOUBLE:
                case OID:
                    if(isNumber == true)
                    {
                        stringBuilder.append(" upper(");
                        stringBuilder.append(field.getName());
                        stringBuilder.append(") = ");
                        stringBuilder.append(search);
                        stringBuilder.append(" or");
                    }
                    break;
                case UNKNOWN:
                case GLOBALID:
                case BLOB:
                case GEOMETRY:
                case RASTER:
                case XML:
                case GUID:
                case DATE:
                    break;
            }
        }
        String result = stringBuilder.toString();
        return result.substring(0,result.length()-2);
    }

    /**
     * 判断是否为数字
     * @param string
     * @return
     */
    public boolean isNumberFunction(String string) {
        boolean result = false;
        Pattern pattern = Pattern.compile("^[-+]?[0-9]");
        if(pattern.matcher(string).matches()){
            //数字
            result = true;
        } else {
            //非数字
        }
        //带小数的
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('^');
        stringBuilder.append('[');
        stringBuilder.append("-+");
        stringBuilder.append("]?[");
        stringBuilder.append("0-9]+(");
        stringBuilder.append('\\');
        stringBuilder.append(".[0-9");
        stringBuilder.append("]+)");
        stringBuilder.append("?$");
        Pattern pattern1 = Pattern.compile(stringBuilder.toString());
        if(pattern1.matcher(string).matches()){
            //数字
            result = true;
        } else {
            //非数字
        }
        return  result;
    }

    /**
     * 初始化图查属性
     */
    private void initMapQuery() {
        mapView.setMagnifierEnabled(true);//放大镜
        if (mapQueryOnTouchListener!=null){
            super.mapView.setOnTouchListener(mapQueryOnTouchListener);
        }
        mapQueryOnTouchListener.clear();//清空当前选择
    }

    /**
     * 恢复默认状态
     */
    private void returnDefault() {
        if (mapQueryOnTouchListener!=null){
            super.mapView.setOnTouchListener(defauleOnTouchListener);//窗口关闭恢复默认点击状态
        }
        mapQueryOnTouchListener.clear();//清空当前选择
        mapView.setMagnifierEnabled(false);//放大镜
    }

}

