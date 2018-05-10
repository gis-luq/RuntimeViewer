package com.gisluq.runtimeviewer.BMOD.SystemModule;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.gisluq.runtimeviewer.Base.BaseActivity;
import com.gisluq.runtimeviewer.R;

import gisluq.lib.Util.AppUtils;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = (TextView)this.findViewById(R.id.activity_about_versionTxt);
        String version = AppUtils.getVersionName(this);
        textView.setText("版本号:"+version);

        TextView txtContext = (TextView)this.findViewById(R.id.activity_about_txtContext);
        String txtContextText = "框架说明";
        txtContext.setText(txtContextText);
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
        TextView title = (TextView) toolbar.findViewById(R.id.activity_baseview_toobar_view_txtTitle);
        title.setPadding(0, 0, 0, 0);
        title.setText("关于");
    }
}
