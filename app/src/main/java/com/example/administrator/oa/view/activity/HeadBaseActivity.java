package com.example.administrator.oa.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
import com.example.administrator.oa.view.bean.ZuzhiUserListResponse;
import com.example.administrator.oa.view.bean.organization_structure.ChildrenBean;
import com.example.administrator.oa.view.bean.organization_structure.OrganizationResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.HttpListener;
import com.example.administrator.oa.view.net.HttpResponseListener;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.viewutils.ViewUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.DialogViewHolder;
import com.luoshihai.xxdialog.XXDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/8.
 */
public abstract class HeadBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llStatubar;
    private RelativeLayout headView;
    private FrameLayout flBaseFrag;

    private RelativeLayout rlBaseheaderBack;
    private RelativeLayout rlBaseheaderHeader;
    private RelativeLayout rlBaseheaderRight;
    private XXDialog mXxDialog;
    public Dialog mLoadingDialog;
    private XXDialog mxxDialog2;
    private XXDialog mxxUsersDialog;
//    public ZuzhiUserBean mZuzhiUserBean;
    private RequestQueue mQueue;
    public String mSetIp="";;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.head_base_activity);
        // 初始化请求队列，传入的参数是请求并发值。
        mQueue = NoHttp.newRequestQueue(1);

        llStatubar = (LinearLayout) findViewById(R.id.ll_statubar);
        headView = (RelativeLayout) findViewById(R.id.rl_head);
        headView.setBackgroundResource(R.color.blue3);
        rlBaseheaderBack = (RelativeLayout) findViewById(R.id.rl_baseheader_back);
        rlBaseheaderHeader = (RelativeLayout) findViewById(R.id.rl_baseheader_header);
        rlBaseheaderRight = (RelativeLayout) findViewById(R.id.rl_baseheader_right);
        flBaseFrag = (FrameLayout) findViewById(R.id.fl_baseFrag);
        View childView = View.inflate(this, getChildLayoutRes(), flBaseFrag);
        //给出抽象方法留给子类实现界面初始化
        rlBaseheaderRight.getChildAt(0).setVisibility(View.GONE);
        ButterKnife.bind(this);
        initView(headView, rlBaseheaderBack, rlBaseheaderHeader, rlBaseheaderRight, childView, llStatubar);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        rlBaseheaderBack.setOnClickListener(clickListener);
        setStatusBar();
        mLoadingDialog = ViewUtils.createLoadingDialog(this, "数据处理中...");
        TipDialog();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 动态设置自定义状态栏的高度
     */
    private void setStatusBar() {
        //先把状态栏设置成透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //测量状态栏高度
        int statusBarHeight = ViewUtils.getStatusBarHeight(this);
        // 获取当前Statubar的布局参数
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) llStatubar.getLayoutParams();
        // 当控件的高强设置成真正庄状态栏的高度
        linearParams.height = statusBarHeight;
        // 使设置好的布局参数应用到控件myGrid
        llStatubar.setLayoutParams(linearParams);

        //设置状态栏为亮色模式
        ViewUtils.StatusBarLightMode(this, true);
        //设置状态栏的底色为纯白色
        llStatubar.setBackgroundColor(getResources().getColor(R.color.blue3));
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
    protected abstract void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter
            , RelativeLayout headerRight, View childView, LinearLayout statubar);


    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    /**
     * 打开Activity，不finish
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * 打开Activity，可以传递参数，不finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 打开Activity，并finish掉
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * 打开Activity，可以传递参数，并finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    // , final boolean hourOrDay
    protected void selectDate(final TextView tv, final String startTime) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (startTime.compareTo(getTime(date)) < 0) {
                    tv.setText(getTime(date));
                } else {
                    Toast.makeText(HeadBaseActivity.this, "结束时间不可早于开始时间", Toast.LENGTH_SHORT).show();
                }
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", "点", "分", "")
                .isCenterLabel(false)
                .setDividerColor(Color.rgb(56, 173, 255))
                .setCancelColor(Color.rgb(252, 252, 252))
                .setSubmitColor(Color.rgb(252, 252, 252))
                .setContentSize(19)
                .setTitleBgColor(Color.rgb(56, 173, 255))//标题背景颜色 Night mode
                .isCyclic(true)//是否循环滚动
                //.setDate(selectedDate)
                //.setRangDate(startDate, endDate)
                .setBackgroundId(0x00808080) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
        if(TextUtils.isEmpty(tv.getText().toString())){
            //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，
            // 避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
            pvTime.setDate(Calendar.getInstance());
        } else {
            // 显示到已经选择的时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            try {
                Date date = sdf.parse(tv.getText().toString());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                pvTime.setDate(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        pvTime.show();
    }

    protected String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    XXDialog mxxDialog;

    public void chooseDate(final List<String> data, final TextView tv, final String title) {
        if(null != mxxDialog){
            mxxDialog.dismiss();
        }
        mxxDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(HeadBaseActivity.this));
                List<String> datas = new ArrayList();
                final CommonRecyclerAdapter<String> adapter = new CommonRecyclerAdapter<String>(HeadBaseActivity.this,
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
                        tv.setText(adapter.getDatas().get(i));
                        mxxDialog.dismiss();
                    }
                });
                tv.setTag("0");
            }
        }.showDialog();
    }


    /****
     * 计算文件大小
     *
     * @param length
     * @return
     */
    public String ShowLongFileSzie(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

    protected void TipDialog() {
        new XXDialog(this, R.layout.dialog_tips) {
            @Override
            public void convert(DialogViewHolder dialogViewHolder) {

            }
        };
    }


    /**
     * 请求网络接口，获取组织结构数据
     *
     * @param title
     */
    public void RequestServerGetZuzhi(final LinearLayout mLlBumen, final TextView textview, final String title,
                                      final CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter) {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<OrganizationResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_ZUZHI, RequestMethod.POST, OrganizationResponse.class);
        request.add("partyStructTypeId", "1");
        ProcessQueue.add(0, request, new OnResponseListener<OrganizationResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<OrganizationResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (response.get().getData().get(0).isOpen()) {
                        chooseDate2(response.get().getData().get(0).getChildren(), title, mLlBumen, textview, mHuiqianAdapter);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<OrganizationResponse> response) {
                Toast.makeText(HeadBaseActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 选择相关机构
     *
     * @param data
     * @param title
     */
    private void chooseDate2(final List<ChildrenBean> data, final String title,
                             final LinearLayout mLlBumen, final TextView textview,
                             final CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter) {
        if(null != mxxDialog2){
            mxxDialog2.dismiss();
        }
        mxxDialog2 = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(HeadBaseActivity.this));
                List<ChildrenBean> datas = new ArrayList();
                final CommonRecyclerAdapter<ChildrenBean> adapter = new CommonRecyclerAdapter<ChildrenBean>(HeadBaseActivity.this,
                        datas, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, final ChildrenBean item, final int i, boolean b) {
                        holder1.setText(R.id.tv, item.getName());
                        if (item.isOpen()) {
                            holder1.getView(R.id.more).setVisibility(View.VISIBLE);
                        } else {
                            holder1.getView(R.id.more).setVisibility(View.GONE);
                        }

                        holder1.getView(R.id.more).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mxxDialog2.dismiss();
                                if (item.isOpen()) {
                                    chooseDate2(item.getChildren(), title, mLlBumen, textview, mHuiqianAdapter);
                                }
                            }
                        });
                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                        RequestServerGetUsers(adapter.getDatas().get(i).getId(),
                                adapter.getDatas().get(i).getName(), textview, mHuiqianAdapter);
                        mxxDialog2.dismiss();
                    }
                });
                mLlBumen.setTag("0");
            }
        }.showDialog();
    }


    /**
     * 请求网络接口，获取组织结构下的具体人员列表
     *
     * @param partyEntityId
     */
    private void RequestServerGetUsers(long partyEntityId, final String departmentName,
                                       final TextView textview,
                                       final CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter) {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<ZuzhiUserListResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_ZUZHI_USERS,
                RequestMethod.POST, ZuzhiUserListResponse.class);
        request.add("partyStructTypeId", "1");
        request.add("partyEntityId", partyEntityId + "");
        ProcessQueue.add(0, request, new OnResponseListener<ZuzhiUserListResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<ZuzhiUserListResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ZuzhiUserBean> tempdata = response.get().getData();
                    if (0 < tempdata.size()) {
                        chooseUsersDate(tempdata, textview, departmentName, mHuiqianAdapter);
                    } else {
                        tempdata.clear();
                        tempdata.add(new ZuzhiUserBean("-1", "没有相关人员"));
                        Toast.makeText(HeadBaseActivity.this, "没有查到相关人员", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ZuzhiUserListResponse> response) {
                Toast.makeText(HeadBaseActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 选择某部门下的具体人员
     *
     * @param data
     * @param tv
     * @param title
     */
    private void chooseUsersDate(final List<ZuzhiUserBean> data, final TextView tv,
                                 final String title,
                                 final CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter) {
        mxxUsersDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(HeadBaseActivity.this));
                List<ZuzhiUserBean> datas = new ArrayList();
                final CommonRecyclerAdapter<ZuzhiUserBean> adapter = new CommonRecyclerAdapter<ZuzhiUserBean>(HeadBaseActivity.this,
                        datas, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, ZuzhiUserBean item, int i, boolean b) {
                        holder1.setText(R.id.tv, item.getName());
                        holder1.getView(R.id.more).setVisibility(View.GONE);
                        holder1.getView(R.id.users).setVisibility(View.GONE);
                        ((ImageView) holder1.getView(R.id.icon)).setImageResource(R.drawable.personal);

                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                        ZuzhiUserBean mZuzhiUserBean = adapter.getDatas().get(i);
                        if (null != mZuzhiUserBean) {
                            mxxUsersDialog.dismiss();
                        }
                        if (null != mHuiqianAdapter) {
                            boolean flag = false;
                            for (int t = 0; t < mHuiqianAdapter.getDatas().size(); t++) {
                                if (mHuiqianAdapter.getDatas().get(t).getId().equals(mZuzhiUserBean.getId())) {
                                    Toast.makeText(HeadBaseActivity.this, "不要重复添加", Toast.LENGTH_SHORT).show();
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                mHuiqianAdapter.add(mZuzhiUserBean);
                            }
                        } else {
                            tv.setText(mZuzhiUserBean.getName());
                            tv.setTag(mZuzhiUserBean.getId());
                        }

                    }
                });
            }
        }.showDialog();
    }

    /**
     * 用来标记取消。
     */
    private Object object = new Object();


    /**
     * 发起请求。
     *
     * @param what      what.
     * @param request   请求对象。
     * @param callback  回调函数。
     * @param canCancel 是否能被用户取消。
     * @param isLoading 实现显示加载框。
     * @param <T>       想请求到的数据类型。
     */
    public <T> void StartRequest(int what, Request<T> request, HttpListener<T> callback,
                            boolean canCancel, boolean isLoading) {
        request.setCancelSign(object);
        mQueue.add(what, request, new HttpResponseListener<>(this, request, callback, canCancel, isLoading));
    }

    @Override
    protected void onDestroy() {
        // 和声明周期绑定，退出时取消这个队列中的所有请求，当然可以在你想取消的时候取消也可以，不一定和声明周期绑定。
        mQueue.cancelBySign(object);

        // 因为回调函数持有了activity，所以退出activity时请停止队列。
        mQueue.stop();
        super.onDestroy();
    }

    protected void cancelAll() {
        mQueue.cancelAll();
    }

    protected void cancelBySign(Object object) {
        mQueue.cancelBySign(object);
    }

}
