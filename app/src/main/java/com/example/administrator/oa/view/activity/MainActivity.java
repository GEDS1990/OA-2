package com.example.administrator.oa.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.common.MyApp;
import com.example.administrator.oa.view.fragment.LiuChongFragment;
import com.example.administrator.oa.view.fragment.MeFragment;
import com.example.administrator.oa.view.fragment.GonggGaoFragment;
import com.example.administrator.oa.view.fragment.MessageFragent;
import com.example.administrator.oa.view.utils.SPUtils;

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
        // TODO Auto-generated method stub
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
}
