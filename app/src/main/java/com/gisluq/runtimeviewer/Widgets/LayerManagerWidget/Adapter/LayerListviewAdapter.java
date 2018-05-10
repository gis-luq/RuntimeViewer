package com.gisluq.runtimeviewer.Widgets.LayerManagerWidget.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.arcgisruntime.layers.Layer;
import com.gisluq.runtimeviewer.R;

import java.util.List;

/**
 * 图表列表
 * Created by luq on 2017/5/5.
 */

public class LayerListviewAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public View itemView;
        public Button btnMore;
        public CheckBox cbxLayer;//图层是否选中
    }

    private List<Layer> layerList =null;
    private Context context;

    public LayerListviewAdapter(Context c, List<Layer> list) {
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
        return layerList.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_layer_managet_layers_item, null);
        holder.itemView = convertView.findViewById(R.id.widget_view_layer_managet_item_view);
        holder.btnMore = (Button)convertView.findViewById(R.id.widget_view_layer_managet_item_btnMore);
        holder.cbxLayer = (CheckBox)convertView.findViewById(R.id.widget_view_layer_managet_item_cbxLayer);
        holder.cbxLayer.setText(layerList.get(index).getName());

        final Layer layer= layerList.get(index);//按照倒序
        holder.cbxLayer.setChecked(layer.isVisible());//设置是否显示

        holder.cbxLayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    layer.setVisible(true);
                }else{
                    layer.setVisible(false);
                }
            }
        });
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(context, v);
                pm.getMenuInflater().inflate(R.menu.menu_layer_tools, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub
                        switch (item.getItemId()) {
                            case R.id.menu_layer_tools_opacity://图层透明度
                                ShowOpacityUtilView(layer);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pm.show();
            }
        });

        return convertView;
    }


    /**
     * 显示透明度操作View
     */
    private void ShowOpacityUtilView(final Layer layer){

        AlertDialog opacityDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate( R.layout.widget_alert_opacity, null);
        TextView txtTitle = (TextView)view.findViewById(R.id.opactiy_element_layout_layerName);

        txtTitle.setText(layer.getName());
        final TextView txtOp = (TextView)view.findViewById(R.id.opactiy_element_layout_layerOpacity);
        float op = layer.getOpacity();
        txtOp.setText(String.valueOf(op));
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.opactiy_element_layout_layerOpactiySeekBar);
        seekBar.setMax(100);
        seekBar.setProgress((int) (op*100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float op = (float)progress/100;
                txtOp.setText(String.valueOf(op));
                layer.setOpacity(op);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        opacityDialog.setView(view);
        opacityDialog.show();
    }
}
