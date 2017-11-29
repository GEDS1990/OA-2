package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.ChatMsgBean;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/10.
 */

public class ChatActivity extends HeadBaseActivity {
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.msg_content)
    EditText mMsgContent;
    @BindView(R.id.btn_fasong)
    Button mBtnFasong;
    @BindView(R.id.msg_content2)
    EditText mMsgContent2;
    @BindView(R.id.btn_fasong2)
    Button mBtnFasong2;
    private List<ChatMsgBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ChatMsgBean> mAdapter;

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_chat;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        intiThisView();
    }

    private void intiThisView() {
        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<ChatMsgBean>(this, datas, R.layout.item_chatmsg) {
            @Override
            public void convert(CommonViewHolder holder, ChatMsgBean item, int position, boolean b) {
                switch (item.getMsgType()) {
                    case 0:
                        ((ImageView) holder.getView(R.id.icon2)).setVisibility(View.GONE);
                        ((ImageView) holder.getView(R.id.icon)).setVisibility(View.VISIBLE);
                        ((ImageView) holder.getView(R.id.icon)).setBackgroundResource(R.drawable.me_headicon);
                        holder.getView(R.id.content2).setVisibility(View.GONE);
                        holder.getView(R.id.content).setVisibility(View.VISIBLE);
                        holder.setText(R.id.content, item.getMsgContent());
                        break;
                    case 1:
                        ((ImageView) holder.getView(R.id.icon)).setVisibility(View.GONE);
                        ((ImageView) holder.getView(R.id.icon2)).setVisibility(View.VISIBLE);
                        ((ImageView) holder.getView(R.id.icon2)).setBackgroundResource(R.drawable.icon_definition_bussiness);
                        holder.getView(R.id.content).setVisibility(View.GONE);
                        holder.getView(R.id.content2).setVisibility(View.VISIBLE);
                        holder.setText(R.id.content2, item.getMsgContent());
                        break;
                }

            }
        };
        mXxre.setAdapter(mAdapter);
    }

    @OnClick({R.id.btn_fasong, R.id.btn_fasong2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_fasong:
                String msg = mMsgContent.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.add(new ChatMsgBean(0, msg));
                    mAdapter.notifyDataSetChanged();
                    mXxre.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
                break;
            case R.id.btn_fasong2:
                String msg2 = mMsgContent2.getText().toString().trim();
                if (TextUtils.isEmpty(msg2)) {
                    Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.add(new ChatMsgBean(1, msg2));
                    mAdapter.notifyDataSetChanged();
                    mXxre.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
                break;
        }
    }

}
