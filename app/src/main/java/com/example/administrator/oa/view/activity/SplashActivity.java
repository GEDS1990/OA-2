package com.example.administrator.oa.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.administrator.oa.view.utils.SPUtils;

/**
 * Created by Administrator on 2017/10/27.
 */

public class SplashActivity extends Activity {

    private boolean mFirst_come;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Handler handler = new Handler();
        mFirst_come = SPUtils.getBoolean(this, "first_come");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFirst_come) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                }
                finish();
            }
        }, 2000);
    }
}
