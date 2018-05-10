package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Listener;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Resource.DrawToolsResource;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Adapter.AlertLayerListAdapter;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Adapter.AttributeAdapter;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Bean.KeyAndValueBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gisluq.lib.Util.ToastUtils;

/**
 * 要素选择事件
 * Created by gis-luq on 2018/4/23.
 */
public class MapSelectOnTouchListener extends DefaultMapViewOnTouchListener {

    private Context context;
    private DrawToolsResource drawToolsResource;
    private boolean isOnLongpress=false;

    private Feature selectFeature;//当前选中要素信息
    private FeatureLayer selectFeatureLayer;//选中要素图层

    public MapSelectOnTouchListener(Context context, MapView mapView) {
        super(context, mapView);
        this.context =context;
    }

    public MapSelectOnTouchListener(Context context, MapView mapView, DrawToolsResource drawToolsResource) {
        super(context, mapView);
        this.context =context;
        this.drawToolsResource =drawToolsResource;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        isOnLongpress =true;
        super.onLongPress(e);
    }

    @Override
    public boolean onUp(MotionEvent e) {

        if (isOnLongpress){
            identifyMapLayers(e);
        }
        isOnLongpress=false;
        return super.onUp(e);
    }

    /**
     * 获取当前选中要素
     * @return
     */
    public Feature getSelectFeature() {
        return selectFeature;
    }

    /**
     * 当前选中要素图层
     * @return
     */
    public FeatureLayer getSelectFeatureLayer() {
        return selectFeatureLayer;
    }

    /**
     * 地图点击查询
     * @param e
     */
    private void identifyMapLayers(MotionEvent e) {
        Point clickPoint = new Point(Math.round(e.getX()), Math.round(e.getY()));
        int tolerance = 5;

        final ListenableFuture<List<IdentifyLayerResult>> identifyFuture = mMapView.identifyLayersAsync(clickPoint,tolerance,false);
        identifyFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Feature> selectFeatureList = new ArrayList<>();
                    List<IdentifyLayerResult> identifyLayersResults = identifyFuture.get();
                    for (IdentifyLayerResult identifyLayerResult : identifyLayersResults) {
                        for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                            identifyLayerResult.getLayerContent();
                            if (identifiedElement instanceof Feature) {
                                Feature identifiedFeature = (Feature) identifiedElement;
                                selectFeatureList.add(identifiedFeature);
                            }
                        }
                    }
                    selectFeature(selectFeatureList);

                    drawToolsResource.gridViewFeatureTemplete.setEnabled(false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 用户选择要素
     * @param selectFeatureList
     */
    private void selectFeature(final List<Feature> selectFeatureList) {
        clearAllFeatureSelect();//清空选择

        int num = selectFeatureList.size();
        if (num==0){
            ToastUtils.showShort(context,"当前没有选中任何要素");
        }else if(num==1){
            FeatureLayer layer = selectFeatureList.get(0).getFeatureTable().getFeatureLayer();
            String layerName = layer.getName();
//            Toast.makeText(context, "选择的图层为：" +layerName , Toast.LENGTH_SHORT).show();
            drawToolsResource.getFeatureEditTools().txtSelectLayerName.setText(layerName);
            setFeatureSelect(selectFeatureList.get(0));

            drawToolsResource.getFeatureEditTools().toolsView.setVisibility(View.VISIBLE);//要素编辑工具
        }else{
            //当前选中要素大于1个图层
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("选择哪个图层要素？");
            //指定下拉列表的显示数据
            AlertLayerListAdapter layerListAdapter = new AlertLayerListAdapter(context,selectFeatureList);
            //设置一个下拉的列表选择项
            builder.setAdapter(layerListAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FeatureLayer layer = selectFeatureList.get(which).getFeatureTable().getFeatureLayer();
                    String layerName = layer.getName();
                    Toast.makeText(context, "当前选择图层：" +layerName , Toast.LENGTH_SHORT).show();
                    drawToolsResource.getFeatureEditTools().txtSelectLayerName.setText(layerName);
                    setFeatureSelect(selectFeatureList.get(which));
                }
            });
            builder.show();

            drawToolsResource.getFeatureEditTools().toolsView.setVisibility(View.VISIBLE);//要素编辑工具
        }
    }

    /**
     * 设置要素选中
     * @param feature
     */
    public void setFeatureSelect(Feature feature) {
        //设置要素选中
        FeatureLayer identifiedidLayer=feature.getFeatureTable().getFeatureLayer();
        identifiedidLayer.setSelectionColor(Color.YELLOW);
        identifiedidLayer.setSelectionWidth(20);
        identifiedidLayer.selectFeature(feature);

        //设置要素属性结果
        List<KeyAndValueBean> keyAndValueBeans = new ArrayList<>();
        Map<String,Object> attributes= feature.getAttributes();
        for (Map.Entry<String, Object> entry:attributes.entrySet()){
            String key=entry.getKey();
            Object object = entry.getValue();
            String value ="";
            if (object!=null){
                value = String.valueOf(object);
            }
            KeyAndValueBean keyAndValueBean = new KeyAndValueBean();
            keyAndValueBean.setKey(key);
            keyAndValueBean.setValue(value);

            keyAndValueBeans.add(keyAndValueBean);
        }

       //选中要素
        selectFeatureLayer = identifiedidLayer;
        selectFeature = feature;

    }

    /**
     * 清空所有要素选择
     */
    public void clearAllFeatureSelect(){
        List<Layer> layers = mMapView.getMap().getOperationalLayers();
        for (int i=0;i<layers.size();i++){
            FeatureLayer featureLayer = (FeatureLayer)layers.get(i);
            featureLayer.clearSelection();
        }
    }

    /**
     * 恢复默认状态
     */
    public void clear(){
        clearAllFeatureSelect();
        drawToolsResource.getFeatureEditTools().txtSelectLayerName.setText("无");
        selectFeatureLayer=null;
        selectFeature=null;
    }


}
