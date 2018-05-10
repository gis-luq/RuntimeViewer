package com.gisluq.runtimeviewer.Widgets.QueryWidget.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.gisluq.runtimeviewer.R;

import java.util.List;

/**
 * 要素查询结果
 * Created by luq on 2017/5/5.
 */

public class QueryResultAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public LinearLayout view;
        public ImageView imageView;
        public TextView textView;//图层
    }

    private List<Feature> list =null;
    private Context context;
    private MapView mapView;

    public QueryResultAdapter(Context c, List<Feature> list, MapView mapView) {
        this.list = list;
        this.context = c;
        this.mapView = mapView;
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        notifyDataSetChanged();//刷新数据
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int index = list.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_query_attributequery_result_item, null);
        holder.view = (LinearLayout)convertView.findViewById(R.id.widget_view_quer_attributequery_result_item_view);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_quer_attributequery_result_item_txtName);

        holder.textView.setText("查询结果"+ (position+1));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFeatureSelect();
                setFeatureSelect(list.get(position));
            }
        });

        return convertView;
    }

    /**
     * 清空所有要素选择
     */
    public void clearAllFeatureSelect(){
        List<Layer> layers = mapView.getMap().getOperationalLayers();
        for (int i=0;i<layers.size();i++){
            FeatureLayer featureLayer = (FeatureLayer)layers.get(i);
            featureLayer.clearSelection();
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

        Geometry buffer = GeometryEngine.buffer(feature.getGeometry(),1000);//缓冲500
        mapView.setViewpointGeometryAsync(buffer);
//        mapView.setViewpointScaleAsync(50000);
    }

}
