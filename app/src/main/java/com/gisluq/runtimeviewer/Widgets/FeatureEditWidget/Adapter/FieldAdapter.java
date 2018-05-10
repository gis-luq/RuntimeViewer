package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.DialogUtils;
import com.gisluq.runtimeviewer.Widgets.QueryWidget.Bean.KeyAndValueBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 属性列表适配器
 * Created by gis-luq on 2017/5/5.
 */
public class FieldAdapter extends BaseAdapter {

    public class AdapterHolder{//列表绑定项
        public View itemView;
        public TextView txtName;
        public EditText txtValue;
//        public CheckBox checkBox;
    }

    private Context context;
    private Feature feature;
    private List<KeyAndValueBean> keyAndValueBeans;

    public FieldAdapter(Context c, Feature feature) {
        this.context = c;
        this.feature =feature;

        //设置要素属性结果
       keyAndValueBeans = new ArrayList<>();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.widget_view_feature_edit_alert_attribute_fielditem, null);
        holder.itemView = convertView.findViewById(R.id.widget_view_feature_edit_alert_attribute_fielditem_view);
        holder.txtName =  convertView.findViewById(R.id.widget_view_feature_edit_alert_attribute_fielditem_txtName);
        holder.txtValue =(EditText) convertView.findViewById(R.id.widget_view_feature_edit_alert_attribute_fielditem_txtValue);

        holder.txtName.setText(keyAndValueBeans.get(position).getKey());
        holder.txtValue.setText(keyAndValueBeans.get(position).getValue());

        holder.txtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                String key = keyAndValueBeans.get(position).getKey();
                keyAndValueBeans.get(position).setValue(value);
                feature.getAttributes().replace(key, value);//更新
            }
        });

//        holder.txtValue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSoftInputFromWindow(context,holder.txtValue);
//            }
//        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String msg = keyAndValueBeans.get(position).getKey()+":"+ keyAndValueBeans.get(position).getValue();
//                DialogUtils.showDialog(context,"字段属性",msg);
//            }
//        });

        return convertView;
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Context context, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
