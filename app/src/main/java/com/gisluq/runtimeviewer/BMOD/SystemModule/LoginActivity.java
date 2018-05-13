package com.gisluq.runtimeviewer.BMOD.SystemModule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.gisluq.runtimeviewer.BMOD.ProjectsModule.View.MainActivity;
import com.gisluq.runtimeviewer.Base.BaseActivity;
import com.gisluq.runtimeviewer.Config.AppWorksSpaceInit;
import com.gisluq.runtimeviewer.Config.SystemDirPath;
import com.gisluq.runtimeviewer.Permission.PermissionsActivity;
import com.gisluq.runtimeviewer.Permission.PermissionsChecker;
import com.gisluq.runtimeviewer.R;
import com.gisluq.runtimeviewer.Utils.FileUtils;

import java.util.List;

import gisluq.lib.LockPatternView.LockPatternView;
import gisluq.lib.Util.SysUtils;


public class LoginActivity extends BaseActivity implements LockPatternView.OnPatternListener {

    private Context context;
    private LockPatternView lock_pattern = null;

    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写入存储
            Manifest.permission.ACCESS_FINE_LOCATION,//位置信息
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA //相机
    };
    private static PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_login);

        this.lock_pattern = (LockPatternView) this.findViewById(R.id.activity_login_lock_pattern);
        this.lock_pattern.setOnPatternListener(this);

        mPermissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }else {
            appInit();
        }
    }

    /***
     * 初始化应用程序状态栏显示
     * @param toolbar
     */
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setNavigationIcon(null);//设置不显示回退按钮
        getLayoutInflater().inflate(R.layout.activity_toobar_view, toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.activity_baseview_toobar_view_txtTitle);
        //判断是否为平板设备
        if (context!=null){
            boolean ispad = SysUtils.isPad(context);//会先执行onCreateCustomToolBar，再执行onCreate
            if (ispad){
                title.setPadding(20, 0, 0, 0);
            }else {
                title.setPadding(50, 0, 0, 0);
            }
        }
        title.setText(getResources().getText(R.string.app_title_name_mian));
    }

    /**
     *  应用程序初始化
     */
    private void appInit() {
        boolean isOk = AppWorksSpaceInit.init(context);//初始化系统文件夹路径
    }

    /**
     * 弹出权限获取提示信息
     */
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }else {
            appInit();
        }
    }

    /**
     * 初始化系统功能菜单栏
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItemSetting= menu.add(Menu.NONE, Menu.FIRST + 1, 0, "锁屏设置");
        MenuItem menuItemAbout= menu.add(Menu.NONE, Menu.FIRST + 2, 0, "关于");
//        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//显示到状态栏
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())//得到被点击的item的itemId
        {
            case Menu.FIRST+1: //对应的ID就是在add方法中所设定的Id
//                ToastUtils.showShort(context,"锁屏设置");
                Intent lockIntent = new Intent(context, LockviewActivity.class);
                context.startActivity(lockIntent);
                break;
            case Menu.FIRST+2: //对应的ID就是在add方法中所设定的Id
//                ToastUtils.showShort(context,"锁屏设置");
                Intent aboutIntent = new Intent(context, AboutActivity.class);
                context.startActivity(aboutIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A new pattern has begun.
     */
    @Override
    public void onPatternStart() {

    }

    /**
     * The pattern was cleared.
     */
    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    /**
     * A pattern was detected from the user.
     *
     * @param pattern The pattern.
     */
    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        String lockStr = LockPatternView.patternToString(pattern);
        String SysConf = SystemDirPath.getLockViewConfPath(context);
        boolean isHave = FileUtils.isExist(SysConf);
        if (isHave) {
            String lockviewMd = FileUtils.openTxt(SysConf);
            if (lockviewMd==null){
                return;
            }
            if (lockviewMd.equals(lockStr)) {
                Intent mainIntent = new Intent(context, MainActivity.class);
                context.startActivity(mainIntent);
                this.finish();
            } else {
                Toast.makeText(context, "解锁解锁失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "程序未设置解锁图案，请设置解锁图案后再试", Toast.LENGTH_SHORT).show();
        }
        this.lock_pattern.clearPattern();// 清空显示
    }
}
