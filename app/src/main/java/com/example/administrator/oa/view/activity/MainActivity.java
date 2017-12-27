package com.example.administrator.oa.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.VersionResponse;
import com.example.administrator.oa.view.common.MyApp;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.fragment.LiuChongFragment;
import com.example.administrator.oa.view.fragment.MeFragment;
import com.example.administrator.oa.view.fragment.GonggGaoFragment;
import com.example.administrator.oa.view.fragment.MessageFragent;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.update.UpdateService;
import com.example.administrator.oa.view.utils.DeviceUtils;
import com.example.administrator.oa.view.utils.SPUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_fragment_container)
    FrameLayout mMainFragmentContainer;
    @BindView(R.id.main_bottome_switcher_container)
    LinearLayout mMainBottomeSwitcherContainer;
    @BindView(R.id.fl_workbench)
    FrameLayout mFlWorkbench;
    @BindView(R.id.fl_manager)
    FrameLayout mFlManager;
    @BindView(R.id.fl_omos)
    FrameLayout mFlOmos;
    @BindView(R.id.fl_mime)
    FrameLayout mFlMime;
    @BindView(R.id.txtImgRed)
    TextView txtImgRed;
    private FragmentManager fragmentManager;
    private MessageFragent fg1;
    private LiuChongFragment fg2;
    private GonggGaoFragment fg3;
    private MeFragment fg4;

    private MyApp application;
    private static String downUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        mFlWorkbench.performClick();

        application = (MyApp) getApplicationContext();
        IntentFilter filter = new IntentFilter(application.mainAction);
        registerReceiver(broadcastReceiver, filter);

        // 检测版本
        checkVersion();
    }

    @OnClick({R.id.fl_workbench, R.id.fl_manager, R.id.fl_omos, R.id.fl_mime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_workbench:
                setChioceItem(0);
                break;
            case R.id.fl_manager:
                setChioceItem(1);
                break;
            case R.id.fl_omos:
                setChioceItem(2);
                break;
            case R.id.fl_mime:
                setChioceItem(3);
                break;
        }
    }

    private void setChioceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                mFlWorkbench.getChildAt(0).setEnabled(false);
                mFlWorkbench.getChildAt(1).setEnabled(false);
                if (fg1 == null) {
                    fg1 = new MessageFragent();
                    fragmentTransaction.add(R.id.main_fragment_container, fg1);
                } else {
                    // 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg1);
                }
                break;
            case 1:
                mFlManager.getChildAt(0).setEnabled(false);
                mFlManager.getChildAt(1).setEnabled(false);
                if (fg2 == null) {
                    fg2 = new LiuChongFragment();
                    fragmentTransaction.add(R.id.main_fragment_container, fg2);
                } else {
                    fragmentTransaction.show(fg2);
                }
                break;
            case 2:
                mFlOmos.getChildAt(0).setEnabled(false);
                mFlOmos.getChildAt(1).setEnabled(false);
                if (fg3 == null) {
                    fg3 = new GonggGaoFragment();
                    fragmentTransaction.add(R.id.main_fragment_container, fg3);
                } else {
                    fragmentTransaction.show(fg3);
                }
                break;
            case 3:
                mFlMime.getChildAt(0).setEnabled(false);
                mFlMime.getChildAt(1).setEnabled(false);
                if (fg4 == null) {
                    fg4 = new MeFragment();
                    fragmentTransaction.add(R.id.main_fragment_container, fg4);
                } else {
                    fragmentTransaction.show(fg4);
                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        mFlWorkbench.getChildAt(0).setEnabled(true);
        mFlWorkbench.getChildAt(1).setEnabled(true);
        mFlManager.getChildAt(0).setEnabled(true);
        mFlManager.getChildAt(1).setEnabled(true);
        mFlOmos.getChildAt(0).setEnabled(true);
        mFlOmos.getChildAt(1).setEnabled(true);
        mFlMime.getChildAt(0).setEnabled(true);
        mFlMime.getChildAt(1).setEnabled(true);
    }

    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (fg1 != null) {
            fragmentTransaction.hide(fg1);
        }
        if (fg2 != null) {
            fragmentTransaction.hide(fg2);
        }
        if (fg3 != null) {
            fragmentTransaction.hide(fg3);
        }
        if (fg4 != null) {
            fragmentTransaction.hide(fg4);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override

        public void onReceive(Context context, Intent intent) {
            showMessageNum();
        }
    };

    /**
     * 显示新消息的数量
     */
    private void showMessageNum(){
        String count = SPUtils.getString(this,"messageCount");
        if (null != count && !"0".equals(count)){
            txtImgRed.setVisibility(View.VISIBLE);
            txtImgRed.setText(count);
        } else {
            txtImgRed.setVisibility(View.GONE);
        }
    }

    /**
     * 检查更新版本
     */
    public void checkVersion() {
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<VersionResponse> request = new JavaBeanRequest<>(UrlConstance.URL_CHECKVERSION,
                RequestMethod.POST, VersionResponse.class);
        //添加url?key=value形式的参数
        request.add("versionNum", DeviceUtils.getVersionName(MainActivity.this));
        request.add("type", 1);
        Queue.add(1, request, new OnResponseListener<VersionResponse>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<VersionResponse> response) {
                if (null != response && null != response.get()) {
                    if(201 == response.get().getCode() && null != response.get().getPath()){
                        downUrl = response.get().getPath();
                        Log.d("versison", downUrl);
                        showVersionDialog();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<VersionResponse> response) {
            }

            @Override
            public void onFinish(int what) {
            }
        });
    }

    /**
     * 提示更新的对话框
     */
    private void showVersionDialog(){
        //发现新版本，提示用户更新
        AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alert.setTitle("软件升级")
                .setMessage("发现新版本,建议立即更新使用.")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 检测权限
                        applyForPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.create().show();
    }

    // 要申请的权限
    protected  String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // 文件请求权限
    protected int REQUESTCODE_REQUEST_PERMISSION = 1002;
    // 手动设置权限
    protected int REQUESTCODE_SETTION_PERMISSION = 1003;
    /**
     * 申请权限
     * @return
     */
    protected void applyForPermission(){
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            } else {
                startUpdateService();
            }
        }
    }

    /**
     * 启动版本更新服务
     */
    protected void startUpdateService() {
        Intent updateIntent = new Intent(MainActivity.this, UpdateService.class);
        updateIntent.putExtra("downUrl", downUrl);
        startService(updateIntent);
    }

    // 提示用户该请求权限的弹出框
    protected void showDialogTipUserRequestPermission() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("下载新版本需要获取存储权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUESTCODE_REQUEST_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_SETTION_PERMISSION) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取
                    int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        // 提示用户应该去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        startUpdateService();
                    }
                }
            }
        }
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE_REQUEST_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
                } else {
                    startUpdateService();
                }
            }
        }
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     */
    protected android.support.v7.app.AlertDialog dialog;
    protected void showDialogTipUserGoToAppSettting() {
        dialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("下载新版本需要获取存储权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUESTCODE_SETTION_PERMISSION);
    }
}
