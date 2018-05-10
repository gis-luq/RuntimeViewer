package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.Symbol;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Resource.DrawToolsResource;

import java.util.List;
import java.util.concurrent.ExecutionException;

import gisluq.lib.Util.ToastUtils;

/**
 * 要素模板
 * Created by luq on 2017/5/5.
 */

public class FeatureTempleteAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public View view;
        public ImageView imageView;
        public TextView textView;//图层
    }

    private List<Layer> layerList =null;
    private Context context;
    private SketchEditor mainSketchEditor;
    private DrawToolsResource drawToolsResource;
    private MapView mapView;

    private int selectItemID = -1;

    public FeatureTempleteAdapter(Context c, MapView mapView, SketchEditor mainSketchEditor, DrawToolsResource drawToolsResource) {
        this.layerList = mapView.getMap().getOperationalLayers();
        this.context = c;
        this.mainSketchEditor = mainSketchEditor;
        this.drawToolsResource = drawToolsResource;
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
        int num=0;
        for (int i=0;i<layerList.size();i++){
            Layer layer = layerList.get(i);
            if (layer.isVisible()){
                num++;
            }
        }
        return num;
    }

    /**
     * 获取选中的Item ID
     * @return
     */
    public Layer getSelectItem(){
        return layerList.get(selectItemID);
    }

    @Override
    public Object getItem(int position) {
        return layerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int index = layerList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_feature_edit_item, null);
        holder.view = convertView.findViewById(R.id.widget_view_feature_edit_item_view);
        holder.imageView =(ImageView) convertView.findViewById(R.id.widget_view_feature_edit_item_img);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_feature_edit_item_txtName);

        //仅获取当前显示的layer
        FeatureLayer layer =null;
        int indexPositon=0;//计数
        for (int i=0;i<layerList.size();i++){
            Layer layerTpl = layerList.get(i);
            if (layerTpl.isVisible()){
                if (indexPositon==position){
                    layer = (FeatureLayer) layerTpl;
                }
                indexPositon++;
            }
        }
        holder.textView.setText(layer.getName());

        //要素模板
        Feature feature = layer.getFeatureTable().createFeature();
        Symbol symbol = layer.getRenderer().getSymbol(feature);
        ListenableFuture<Bitmap> bitmap =symbol.createSwatchAsync(context, Color.WHITE);
        try {
            holder.imageView.setImageBitmap(bitmap.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        holder.imageView.setBackground(null);//layout中要素模板背景置为空

        final FeatureLayer finalLayer = layer;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(context,"当前选中图层模板："+ finalLayer.getName()+",点击屏幕开始绘制要素");

                selectItemID = position;//保存选中item
                drawToolsResource.getFeatureAddTools().toolsView.setVisibility(View.VISIBLE);//显示新建要素工具
                drawToolsResource.getFeatureAddTools().txtSelectLayerName.setText(finalLayer.getName());
                mapView.setMagnifierEnabled(false);//放大镜

                GeometryType geometryType = finalLayer.getFeatureTable().getGeometryType();
                switch (geometryType){
                    case POINT:
                    case MULTIPOINT:
                        mainSketchEditor.start(SketchCreationMode.POINT);
                        break;
                    case POLYLINE:
                        mainSketchEditor.start(SketchCreationMode.POLYLINE);
                        break;
                    case POLYGON:
                    case ENVELOPE:
                        mainSketchEditor.start(SketchCreationMode.POLYGON);
                        break;
                    case UNKNOWN:
                        ToastUtils.showShort(context,"当前图层："+ finalLayer.getName()+",数据类型未知");
                        break;
                }
            }
        });

        return convertView;
    }

}
