package com.gisluq.runtimeviewer.Widgets.FeatureEditWidget.Media;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.TimeUtils;

import gisluq.lib.Util.ToastUtils;


/**
 * 自定义用户弹窗组件
 * Created by lq on 2015/11/24.
 */
public class VoiceAlertView {

    private Context context;
    private TextView titleTxtView;//标题栏
    private TextView contextTxtView;//内容信息
    private TextView timerTxtView;//计时器

    private Button btnVoice;//录音
    private ImageButton btnVoiceStart;
    private ImageButton btnVoiceStop;
    private AlertDialog alertDialog = null;//状态窗口

    private AudioRecoderUtils mAudioRecoderUtils;
    static final int VOICE_REQUEST_CODE = 66;
    private String name_guid;

    public VoiceAlertView(final Context context, String audioPath, String name){
        this.context = context;
        this.name_guid = name;
        alertDialog = new AlertDialog.Builder(context).create();
        View view =  LayoutInflater.from(context).inflate( R.layout.widget_view_feature_edit_alert_voice, null);

        this.titleTxtView = (TextView)view.findViewById(R.id.widget_view_feature_edit_alert_voice_titleTxt);
        this.contextTxtView = (TextView)view.findViewById(R.id.widget_view_feature_edit_alert_voice_contextTxt);
        this.btnVoice = (Button)view.findViewById(R.id.widget_view_feature_edit_alert_voice_btnVoice);
        this.btnVoiceStart = view.findViewById(R.id.widget_view_feature_edit_alert_voice_btnVoiceStart);
        this.btnVoiceStop = view.findViewById(R.id.widget_view_feature_edit_alert_voice_btnVoiceStop);

        this.timerTxtView = (TextView)view.findViewById(R.id.widget_view_feature_edit_alert_voice_txtTime);

        mAudioRecoderUtils = new AudioRecoderUtils(audioPath);

        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                timerTxtView.setText(TimeUtils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                Toast.makeText(context, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                timerTxtView.setText(TimeUtils.long2String(0));
            }
        });

        StartListener();

        //6.0以上需要权限申请
        requestPermissions();

        alertDialog.setView(view);
    }

    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions() {
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                ) {
            StartListener();

            //判断是否开启语音权限
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }

    }

    /**
     * 设置录音
     */
    private void StartListener() {
        this.btnVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        contextTxtView.setText("松开按钮保存");
                        btnVoice.setBackgroundResource(R.mipmap.voice_presed);
                        timerTxtView.setVisibility(View.VISIBLE);
                        mAudioRecoderUtils.startRecord(name_guid);

                        btnVoiceStart.setEnabled(false);
                        btnVoiceStop.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        contextTxtView.setText("按住按钮说话");
                        btnVoice.setBackgroundResource(R.mipmap.voice_default);
                        timerTxtView.setVisibility(View.INVISIBLE);
                        mAudioRecoderUtils.stopRecord();

                        btnVoiceStart.setEnabled(true);
                        btnVoiceStop.setEnabled(true);
                        break;
                }
                return true;
            }
        });

        this.btnVoiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isActive = mAudioRecoderUtils.isActie();
                if (isActive){
                    ToastUtils.showShort(context,"当前已经处于录音状态");
                }else {
                    btnVoice.setEnabled(false);

                    timerTxtView.setVisibility(View.VISIBLE);
                    mAudioRecoderUtils.startRecord(name_guid);
                }
            }
        });

        this.btnVoiceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isActive = mAudioRecoderUtils.isActie();
                if (isActive==false){
                    ToastUtils.showShort(context,"当前已经处于未录音状态，请点击开始后再试");
                }else {
                    btnVoice.setEnabled(true);
                    timerTxtView.setVisibility(View.INVISIBLE);
                    mAudioRecoderUtils.stopRecord();
                }
            }
        });

    }

    /**
     * 显示
     */
    public void show(){
        alertDialog.show();
    }

    /**
     * 隐藏
     */
    public void hide(){
        alertDialog.hide();
    }

    /**
     * 设置标题
     */
    public void setTitle(String title){
        this.titleTxtView.setText(title);
    }




}
