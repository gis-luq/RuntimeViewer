package com.gisluq.runtimeviewer.Widgets.QueryWidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.DialogUtils;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Bean.KeyAndValueBean;

import java.util.List;

/**
 * 属性列表适配器
 * Created by gis-luq on 2017/5/5.
 */
public class AttributeAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public View itemView;
        public TextView txtName;
        public TextView txtValue;
//        public CheckBox checkBox;
    }

    private Context context;
    private List<KeyAndValueBean> keyAndValueBeans;

    public AttributeAdapter(Context c, List<KeyAndValueBean> keyAndValueBeans) {
        this.context = c;
        this.keyAndValueBeans = keyAndValueBeans;
    }


    /**
     * 刷新数据
     */
    public void refreshData(){
        try {
            notifyDataSetChanged();//刷新数据
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        if (keyAndValueBeans ==null){
            return 0;
        }else {
            return keyAndValueBeans.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AdapterHolder holder = new AdapterHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_query_mapquery_fielditem, null);
        holder.itemView = convertView.findViewById(R.id.widget_view_query_mapquery_fielditem_view);
        holder.txtName = (TextView) convertView.findViewById(R.id.widget_view_query_mapquery_fielditem_txtName);
        holder.txtValue = (TextView)convertView.findViewById(R.id.widget_view_query_mapquery_fielditem_txtValue);

        holder.txtName.setText(keyAndValueBeans.get(position).getKey());
        holder.txtValue.setText(keyAndValueBeans.get(position).getValue());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = keyAndValueBeans.get(position).getKey()+":"+ keyAndValueBeans.get(position).getValue();
                DialogUtils.showDialog(context,"字段属性",msg);
            }
        });

        return convertView;
    }
}
