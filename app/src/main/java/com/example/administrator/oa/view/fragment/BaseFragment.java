package com.example.administrator.oa.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.utils.viewutils.ViewUtils;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/7.
 */
public abstract class BaseFragment extends Fragment {

    protected View rootView;
    protected FrameLayout fl_fragmentContainer;
    private LinearLayout mHeadview;
    private LinearLayout mMyStatusBar;
    private String curFragmentTag;
    private RelativeLayout rlBaseheaderBack;
    private RelativeLayout rlBaseheaderHeader;
    private RelativeLayout rlBaseheaderRight;
    public Dialog mLoadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 考虑复用fragment对象，使用单例模式
         */
        if (rootView == null) {
            //根布局
            rootView = inflater.inflate(R.layout.basefragment, container, false);
            //由于标题栏中的数据在每个fragment中有不一样的,以下几个留给子类自己去实现
            mHeadview = ((LinearLayout) rootView.findViewById(R.id.rl_head));
            //设置头布局颜色（子类也可以重写来换颜色）
            mHeadview.setBackgroundColor(getResources().getColor(R.color.blue3));
            mMyStatusBar = ((LinearLayout) rootView.findViewById(R.id.ll_statubar));
            //布局占位容器
            fl_fragmentContainer = ((FrameLayout) rootView.findViewById(R.id.fl_baseFrag));

            rlBaseheaderBack = (RelativeLayout) rootView.findViewById(R.id.rl_baseheader_back);
            rlBaseheaderHeader = (RelativeLayout) rootView.findViewById(R.id.rl_baseheader_header);
            rlBaseheaderRight = (RelativeLayout) rootView.findViewById(R.id.rl_baseheader_right);

            //实现类需要把自己的具体布局填充到FrameLayout占位容器中
            //提供方法获取实现类的布局文件
            int childLayout = getChildLayoutRes();
            View childView = inflater.inflate(childLayout, fl_fragmentContainer);
            //头布局资源

            ButterKnife.bind(this, childView);
            initView(mHeadview, rlBaseheaderBack, rlBaseheaderHeader, rlBaseheaderRight, childView, mMyStatusBar);
            //给出抽象方法留给子类实现界面初始化
            setStatusBar();
            mLoadingDialog = ViewUtils.createLoadingDialog(getContext(), "数据处理中...");
        }

        onCreateChilView(savedInstanceState);
        return rootView;
    }

    protected void onCreateChilView(Bundle savedInstanceState) {

    }


    /**
     * 动态设置自定义状态栏的高度
     */
    private void setStatusBar() {
        //先把状态栏设置成透明
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //测量撞他爱栏高度
        int statusBarHeight = ViewUtils.getStatusBarHeight(getActivity());
        // 取控件mGrid当前的布局参数
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mMyStatusBar.getLayoutParams();
        // 当控件的高强制设成50象素
        linearParams.height = statusBarHeight;
        // 使设置好的布局参数应用到控件myGrid
        mMyStatusBar.setLayoutParams(linearParams);
        mMyStatusBar.setBackgroundResource(R.color.blue3);
    }

    /**
     * 获取实现类的布局资源
     *
     * @return
     */
    protected abstract int getChildLayoutRes();

    /**
     * 实现类需要把自己的具体布局填充到FrameLayout占位容器中
     *
     * @param childView
     */
    protected abstract void initView(View childHeadView, RelativeLayout rlBaseheaderBack, RelativeLayout rlBaseheaderHeader,
                                     RelativeLayout rlBaseheaderRight, View childView, LinearLayout myStatusBar);

    /**
     * 打开Activity，不finish
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(getContext(), clazz);
        startActivity(intent);
    }

    /**
     * 打开Activity，可以传递参数，不finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
