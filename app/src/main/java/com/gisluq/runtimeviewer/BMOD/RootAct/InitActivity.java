package com.gisluq.runtimeviewer.BMOD.RootAct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gisluq.runtimeviewer.BMOD.SystemModule.LoginActivity;
import com.gisluq.runtimeviewer.Config.AppWorksSpaceInit;
import com.gisluq.runtimeviewer.R;

import gisluq.lib.Util.AppUtils;
import gisluq.lib.Util.SysUtils;
import gisluq.lib.Util.ToastUtils;

/**
 *  应用程序初始化页面
 */
public class InitActivity extends AppCompatActivity {

    private static String TAG = "InitActivity";
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟时间
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_init);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //判断是否为平板设备
                    boolean ispad = SysUtils.isPad(context);
                    if (ispad){
                        startActivity();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("检测到当前设备并非平板，继续安装此应用程序将会出现异常，是否任然继续安装此应用程序？");
                        builder.setTitle("系统提示");
                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                InitActivity.this.finish();
                            }
                        });
                        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity();
                                dialog.dismiss();
                                ToastUtils.showShort(context,"应用程序打开失败，请使用平台后再试");
                            }
                        });
                        builder.setCancelable(false);//点击外部不消失
                        builder.create().show();
                    }
                }catch (Exception e){
                    Log.e(TAG,e.toString());
                }

                startActivity();


            }

        }, SPLASH_DISPLAY_LENGHT);

        TextView textView = (TextView)this.findViewById(R.id.activity_init_versionTxt);
        String version = AppUtils.getVersionName(this);
        textView.setText("版本号:"+version);
    }

    /**
     * 跳转到登录页
     */
    private void startActivity() {
        Intent loginIntent = new Intent(context,
                LoginActivity.class);
        context.startActivity(loginIntent);
        ((Activity)context).finish();
    }

}
