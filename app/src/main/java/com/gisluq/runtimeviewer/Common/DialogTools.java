package com.gisluq.runtimeviewer.Common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 弹窗工具组件
 * Created by luq on 2017/4/29.
 */

public class DialogTools {

    /**
     * 系统弹窗提示信息
     */
    public static void showSystemDialog(Context context,
                                        String title,
                                        String msg,
                                        String negativeButtonName,
                                        DialogInterface.OnClickListener negativeButtonlistener,
                                        String positiveButtonName,
                                        DialogInterface.OnClickListener positiveButtonlistener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        //确定按钮
        if (negativeButtonName!=null){
            builder.setNegativeButton(negativeButtonName, negativeButtonlistener);
        }
        //取消按钮
        if (positiveButtonName!=null){
            builder.setPositiveButton(positiveButtonName, positiveButtonlistener);
        }

        builder.create().show();

    }
}
