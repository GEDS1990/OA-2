package com.example.administrator.oa.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.adapter.ViewPagerAdapter;
import com.example.administrator.oa.view.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public class GuideActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {


    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    //引导图片资源
    private static final int[] pics = {R.drawable.defout2,R.drawable.defout1, R.drawable.defout3};

    //底部小店图片
    private ImageView[] dots;

    //记录当前选中位置
    private int currentIndex;
    private Button mGuideBtn;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        views = new ArrayList<>();
        mGuideBtn = ((Button) findViewById(R.id.guide_btn));
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        vp = (ViewPager) findViewById(R.id.viewpager);
        //初始化Adapter
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        //绑定回调
        vp.setOnPageChangeListener(this);

        //初始化底部小点
        initDots();
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);//都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
    }

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }

    /**
     * 这只当前引导小点的选中
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }

        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    //当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        //设置底部小点选中状态
        setCurDot(arg0);
        if (arg0 == 2) {
            mGuideBtn.setVisibility(View.VISIBLE);
            mGuideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                    SPUtils.put(GuideActivity.this,"first_come",true);
                    finish();
                }
            });
        } else {
            mGuideBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }
}
