package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.ChatMsgListBean;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/12.
 */

public class ChatMsgListActivity extends HeadBaseActivity {
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    private CommonRecyclerAdapter<ChatMsgListBean> mAdapter;
    private List<ChatMsgListBean> datas = new ArrayList<>();

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_chatmsglist;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("聊天消息");
        initThisView();
    }

    private void initThisView() {
        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<ChatMsgListBean>(this, datas, R.layout.item_message) {
            @Override
            public void convert(CommonViewHolder holder, ChatMsgListBean item, int position, boolean b) {
                holder.setText(R.id.name, item.getName());
                holder.setText(R.id.date, item.getTime());
                holder.setText(R.id.msg_content, item.getLatestMsg());
                //((TextView) holder.getView(R.id.iv)).setBackgroundResource(item.getPicRes());

                if (!TextUtils.isEmpty(item.getName())) {
                    String firstStr = String.valueOf(item.getName().charAt(0));
                    holder.setText(R.id.iv, firstStr);
                }
            }
        };
        mXxre.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                readyGo(ChatActivity.class);
            }
        });

        mAdapter.add(new ChatMsgListBean("周一", "张飞", "欺我无谋？定要尔等血偿！谁！还敢过来一战？！", R.drawable.zhangfei));
        mAdapter.add(new ChatMsgListBean("06-12 12:12", "刘备", "与先生相识方觉眼界大开，却不想相聚如此之短", R.drawable.liubei));
        mAdapter.add(new ChatMsgListBean("13:44", "赵子龙", "即使敌众我寡，末将亦能万军丛中取敌将首级！", R.drawable.zhaoxin));
        mAdapter.add(new ChatMsgListBean("周天", "诸葛亮", "兵卒有制，虽庸将未败;若兵卒自乱，虽贤将危之。", R.drawable.zhugeliang));
        mAdapter.add(new ChatMsgListBean("周天", "Smart", "hello today is a happy day!", R.drawable.zhugeliang));
    }

}
