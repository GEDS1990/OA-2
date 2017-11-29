package com.example.administrator.oa.view.activity;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.DialogViewHolder;
import com.luoshihai.xxdialog.XXDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/15.
 */

public class QingJiaActivity2 extends HeadBaseActivity {
    @BindView(R.id.web)
    WebView mWeb;
    @BindView(R.id.btn1)
    Button mBtn1;
    @BindView(R.id.btn2)
    Button mBtn2;
    @BindView(R.id.tv1)
    TextView mTv1;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_qingjia2;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("请假申请");
        initThisView();
    }

    private void initThisView() {
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.loadUrl("file:///android_asset/index.html");
        mWeb.addJavascriptInterface(this, "android");
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
                //想在页面开始加载时有操作，在这添加
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //想在页面加载结束时有操作，在这添加
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候WebView我打开，为false则系统浏览器或第三方浏览器打开
                //如果要下载页面中的游戏或者继续点击网页中的链接进入下一个网页的话，重写此方法下
                //不然就会跳到手机自带的浏览器了，而不继续在你这个webview里面展现了
                //startActivity(new Intent(FashionHtmlActivity.this,));
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                //想在收到错误信息的时候，执行一些操作，走此方法
            }
        });
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWeb.canGoBack()) {  // goBack()表示返回WebView的上一页面
            mWeb.goBack();
            finish();
            return true;
        } else {
            //结束当前页
            return super.onKeyDown(keyCode, event);
        }
    }

    @OnClick({R.id.btn1, R.id.btn2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                // 无参数调用 JS的方法
                mWeb.loadUrl("javascript:javacalljs()");
                break;
            case R.id.btn2:
                mWeb.loadUrl("javascript:javacalljswith(" + "'http://blog.csdn.net/Leejizhou'" + ")");
                break;
        }
    }

    //由于安全原因 targetSdkVersion>=17需要加 @JavascriptInterface
    //JS调用Android JAVA方法名和HTML中的按钮 onclick后的别名后面的名字对应
    @JavascriptInterface
    public void startFunction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QingJiaActivity2.this, "", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                List<String> datas_gongju = new ArrayList();
                datas_gongju.add("飞机");
                datas_gongju.add("火车");
                datas_gongju.add("汽车");
                chooseDate(datas_gongju, text);
            }
        });


    }

    XXDialog mxxDialog;

    public void chooseDate(final List<String> data, final String title) {
        mxxDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(QingJiaActivity2.this));
                List<String> datas = new ArrayList();
                final CommonRecyclerAdapter<String> adapter = new CommonRecyclerAdapter<String>(QingJiaActivity2.this,
                        datas, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, String item, int i, boolean b) {
                        holder1.setText(R.id.tv, item);
                        holder1.getView(R.id.more).setVisibility(View.GONE);
                        holder1.getView(R.id.users).setVisibility(View.GONE);
                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                        mWeb.loadUrl("javascript:javacalljswith2(" +"'"+ adapter.getDatas().get(i)+"'"+  ")");
                        mxxDialog.dismiss();
                    }
                });
            }
        }.showDialog();
    }
}
