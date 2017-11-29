package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.oa.R;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/11.
 */

public class ShemiHetongActivity extends HeadBaseActivity {
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.beizhu)
    EditText mBeizhu;
    @BindView(R.id.main_content)
    EditText mMainContent;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.add)
    TextView mAdd;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private List<Object> datas = new ArrayList<>();
    private CommonRecyclerAdapter<Object> mAdapter;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_shemi_hetong;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("涉密合同申请单");
        initThisView();
    }

    private void initThisView() {
        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<Object>(this, datas, R.layout.item_shemihetong) {
            @Override
            public void convert(CommonViewHolder holder, final Object item, int position, boolean b) {

                holder.getView(R.id.delet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.remove(item);
                    }
                });
            }
        };
        mXxre.setAdapter(mAdapter);
        mAdapter.add(new Object());
    }

    @OnClick({R.id.add, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                mAdapter.add(new Object());
                break;
            case R.id.btn_caogao:
                break;
            case R.id.btn_commit:
                break;
        }
    }
}
