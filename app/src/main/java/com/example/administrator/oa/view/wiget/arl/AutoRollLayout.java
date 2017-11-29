package com.example.administrator.oa.view.wiget.arl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.utils.viewutils.ThreadUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinchao on 2016/07/19.
 */
public class AutoRollLayout extends RelativeLayout {

    private LinearLayout dotContainer;
    private ViewPager viewPager;
    private TextView titleTv;
    private GestureDetector gestureDetector;
    public int currentIndex;

    public AutoRollLayout(Context context) {
        this(context, null);
    }

    public AutoRollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoRollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(getContext(), R.layout.arl_layout_arl, this);
        dotContainer = (LinearLayout) findViewById(R.id.arl_arl_dot_container);
        viewPager = (ViewPager) findViewById(R.id.arl_arl_vp);
        titleTv = (TextView) findViewById(R.id.arl_arl_title_tv);
        viewPager.setOnTouchListener(touchListener);
        gestureDetector = new GestureDetector(getContext(), gestureListener);
        viewPager.setPageTransformer(true, new ViewPagerTrans());

        /**
         * 给viewpager设置切换动画(方法2的动画效果)
         */
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        /**
         * 怎么设置viewpager在使用setCurrentItem的滑动速度
         * 解决:重写scroller类
         */
        ViewPagerScroller scroller = new ViewPagerScroller(context);
        scroller.setScrollDuration(1500);
        scroller.initViewPagerScroll(viewPager);//这个是设置切换过渡时间为2秒

    }
    public void setShowDotContainer(boolean b) {
        if (!b) {
            dotContainer.setVisibility(GONE);
        }
    }

    private ImageView.ScaleType scaleType;
    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }
    private List<RollItem> items;

    public void setItems(List<RollItem> items) {
        this.items = items;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(pageAdapter);
                //titleTv.setText(rollItem.getTitle());
            }
        });

        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleTv.setText(null);
            }
        });

        viewPager.setOnPageChangeListener(pageListener);
        dotContainer.removeAllViews();
        addDots();
        pageListener.onPageSelected(0);
    }


    static Handler handler = new Handler();
    boolean autoRoll = false;

    public void setAutoRoll(boolean autoRoll) {
        this.autoRoll = autoRoll;
        if (autoRoll) {
            handler.postDelayed(showNextPageRunnable, 4000);
        } else {
            handler.removeCallbacks(showNextPageRunnable);
        }
    }

    Runnable showNextPageRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            if (!isTouching) {
                showNextPage();
            }
            handler.postDelayed(this, 4000);
        }
    };
    boolean toRight = true;

    private void showNextPage() {
        if (pageAdapter.getCount() == 1) {
            return;
        }
        currentIndex = viewPager.getCurrentItem();

        if (currentIndex == 0) {
            toRight = true;
        } else if (currentIndex == pageAdapter.getCount() - 1) {
            toRight = false;
        }
        int targetIndex;
        if (toRight) {
            targetIndex = currentIndex + 1;
        } else {
            targetIndex = currentIndex - 1;
            //targetIndex = 0;
        }
        viewPager.setCurrentItem(targetIndex);
    }


    private void addDots() {
        if (items == null || items.isEmpty()) {
            return;
        }
        int pxFor10dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
        for (RollItem item : items) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pxFor10dp, pxFor10dp);
            lp.setMargins(0, 0, 20, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.arl_dot_selector);
            dot.setOnClickListener(chooseItemOcl);
            dotContainer.addView(dot);
        }
    }

    PagerAdapter pageAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        List<ImageView> cache = new ArrayList<>();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (cache.isEmpty()) {
                ImageView iv = new ImageView(container.getContext());
                iv.setScaleType(scaleType == null?ImageView.ScaleType.CENTER_CROP:scaleType);
                cache.add(iv);
            }
            ImageView imageView = cache.remove(0);
            imageView.setScaleType(scaleType == null?ImageView.ScaleType.CENTER_CROP:scaleType);
//            Picasso.with(container.getContext())
//                    .load(items.get(position)
//                            .getPicPath())
//                    .placeholder(R.drawable.loading_frame_anim)
//                    .noFade()
//                    .config(Bitmap.Config.RGB_565)
//                    .into(imageView);
            Picasso.with(container.getContext()).load(items.get(position).getPicPath()).config(Bitmap.Config.RGB_565).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView = (ImageView) object;
            imageView.setScaleType(scaleType == null?ImageView.ScaleType.CENTER_CROP:scaleType);
            cache.add(imageView);
            container.removeView(imageView);
        }
    };
    private ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (items == null || items.isEmpty()) {
                return;
            }
            RollItem rollItem = items.get(position);
            //titleTv.setText(rollItem.getTitle());
            titleTv.setText("");
            for (int i = 0; i < pageAdapter.getCount(); i++) {
                if (dotContainer.getChildAt(i) != null) {
                    dotContainer.getChildAt(i).setEnabled(i != position);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    OnClickListener chooseItemOcl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(dotContainer.indexOfChild(v));
        }
    };
    boolean isTouching;

    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    isTouching = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isTouching = false;
                    break;
            }
            return false;
        }
    };

    public interface ViewPagerOnClickListener {
        public void viewPageronClick();
    }

    private ViewPagerOnClickListener viewPagerOnClickListener;

    public void setViewPagerOnClickListener(ViewPagerOnClickListener viewPagerOnClickListener) {
        this.viewPagerOnClickListener = viewPagerOnClickListener;
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {

            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            AutoRollLayout.this.performClick();
            if (viewPagerOnClickListener != null) {
                viewPagerOnClickListener.viewPageronClick();
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    class ViewPagerTrans implements ViewPager.PageTransformer {

        private float MIN_ALPHA = 0.5f;//渐变
        private float MIN_SCALE = 0.8f;//缩放

        @Override
        public void transformPage(View page, float position) {
//            float alpha = (1 - Math.abs(position)) * MIN_ALPHA + MIN_ALPHA;
//            float leftScale =  MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            if (position < -1) {//左侧划出屏幕部分
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) {
                if (position <= 0) {//滑动过程中左半部分  [-1~0]
                    float leftAlpha = (1 + position) * MIN_ALPHA + MIN_ALPHA;
                    page.setAlpha(leftAlpha);

                    float leftScale = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(leftScale);
                    page.setScaleY(leftScale);
                } else {//滑动过程中又半部分 [0~1]
                    float rightAlpha = (1 - position) * MIN_ALPHA + MIN_ALPHA;
                    page.setAlpha(rightAlpha);

                    float rightScale = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(rightScale);
                    page.setScaleY(rightScale);
                }
            } else {//右侧超出屏幕部分
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }
}
