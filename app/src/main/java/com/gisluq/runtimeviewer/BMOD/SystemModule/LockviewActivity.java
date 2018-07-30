package com.gisluq.runtimeviewer.BMOD.SystemModule;

import android.content.Context;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.gisluq.runtimeviewer.Base.BaseActivity;
import com.gisluq.runtimeviewer.Config.SystemDirPath;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;

import java.util.List;

import gisluq.lib.LockPatternView.LockPatternView;

public class LockviewActivity extends BaseActivity implements LockPatternView.OnPatternListener {

    private Context context=null;
    private LockPatternView lock_pattern = null;
    private TextView titleTxt = null;

    private static String txtTag0 = "请输入原始解锁手势";
    private static String txtTag1 = "创建解锁手势";
    private static String txtTag2 = "再次输入解锁手势";
    private static String txtTag3 = "解锁手势设置完成";

    private int tagIndex = 0;

    private String lockViewMD = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockview);

        context = this;

        this.lock_pattern = (LockPatternView)this.findViewById(R.id.activity_lock_view_pattern);
        this.lock_pattern.setLockDefaultColorDeep(true);
        this.lock_pattern.setOnPatternListener(this);

        this.titleTxt = (TextView)this.findViewById(R.id.activity_lock_view_title);

        //检查是否存在密码
        String SysConf = SystemDirPath.getLockViewConfPath(context);
        boolean isHave = FileUtils.isExist(SysConf);
        if(isHave){
            this.titleTxt.setText(txtTag0);//输入解锁手势
            tagIndex=0;
        }else{
            this.titleTxt.setText(txtTag1);//创建解锁手势
            tagIndex=1;
        }
    }

    /***
     * 初始化应用程序状态栏显示
     *
     * @param toolbar
     */
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
//        toolbar.setNavigationIcon(null);//设置不显示回退按钮
        getLayoutInflater().inflate(R.layout.activity_toobar_view, toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.activity_baseview_toobar_view_txtTitle);
        title.setPadding(0, 0, 0, 0);
        title.setText("锁屏设置");
    }

    /**
     * 退出系统
     */
    private void exitActivity() {
        this.finish();
    }

    @Override
    public void onPatternStart() {

    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {

        String lockView = LockPatternView.patternToString(pattern);
        if (tagIndex==0){
            String SysConf = SystemDirPath.getLockViewConfPath(context);
            String Md = FileUtils.openTxt(SysConf,"GB2312");
            if(Md.equals(lockView)){
                Toast.makeText(context, "解锁验证通过", Toast.LENGTH_SHORT).show();
                this.titleTxt.setText(txtTag1);
            }else{
                tagIndex=-1;
                Toast.makeText(context, "解锁验证失败", Toast.LENGTH_SHORT).show();
            }
        }

        if(tagIndex==1){
            this.lockViewMD = lockView;
            this.titleTxt.setText(txtTag2);
        }else if(tagIndex==2){
            String md = LockPatternView.patternToString(pattern);
            if(md.equals(lockViewMD)){
                this.titleTxt.setText(txtTag3);
                tagIndex=0;
                setLockViewMD(md);
            }else{
                tagIndex=0;
                Toast.makeText(context, "两次输入密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        titleTxt.setText(txtTag1);
                    }
                }, 1200);
            }
        }
        tagIndex++;
        this.lock_pattern.clearPattern();
    }

    /**
     * 设置登陆秘钥
     * @param lockViewMD
     */
    private void setLockViewMD(String lockViewMD) {
        if(lockViewMD!=null){
            //创建系统配置文件Sys.conf
            String SysConf = SystemDirPath.getLockViewConfPath(context);
            FileUtils.saveTxt(SysConf, lockViewMD);
            Toast.makeText(context, "解锁图案设置成功", Toast.LENGTH_SHORT).show();
            this.setResult(11);
        }else {
            Toast.makeText(context, "解锁图案设置失败", Toast.LENGTH_SHORT).show();
            this.setResult(10);
        }

        this.finish();
    }

}
