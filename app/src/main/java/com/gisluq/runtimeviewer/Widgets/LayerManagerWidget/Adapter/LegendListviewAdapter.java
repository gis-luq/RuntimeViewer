package com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.SymbolStyle;
import com.gisluq.runtimeviewer.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 图例
 * Created by gis-luq on 2018/4/25.
 */
public class LegendListviewAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public ImageView imageView;
        public TextView textView;//图层
    }

    private List<Layer> layerList =null;
    private Context context;

    public LegendListviewAdapter(Context c, List<Layer> list) {
        this.layerList = list;
        this.context = c;
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

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int index = layerList.size()-position-1;
        if (index<0) return convertView;//为空

        AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_layer_managet_legent_item, null);
        holder.imageView =(ImageView) convertView.findViewById(R.id.widget_view_layer_managet_legent_item_img);
        holder.textView = (TextView) convertView.findViewById(R.id.widget_view_layer_managet_legent_item_txtName);

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
        return convertView;
    }

}
