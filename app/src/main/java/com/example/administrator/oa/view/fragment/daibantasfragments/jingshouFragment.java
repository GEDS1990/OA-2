package com.example.administrator.oa.view.fragment.daibantasfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.MsgDetailActivity;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/28.
 */

public class jingshouFragment extends Fragment {

    private String mTitle;
    private List<Object> datas = new ArrayList<>();
    private CommonRecyclerAdapter<Object> mAdapter;

    public static jingshouFragment getInstance(String title) {
        jingshouFragment sf = new jingshouFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_simple_card, null);
        XXRecycleView xxre = (XXRecycleView) view.findViewById(R.id.xxre);

        xxre.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommonRecyclerAdapter<Object>(getContext(), datas, R.layout.item_message) {
            @Override
            public void convert(CommonViewHolder commonViewHolder, Object o, int i, boolean b) {

            }
        };
        xxre.setAdapter(mAdapter);

        //getMsgDatas();
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                Intent intent = new Intent(getContext(), MsgDetailActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void getMsgDatas() {
        datas.clear();
        for (int i = 0; i < 5; i++) {
            datas.add(new Object());
        }
        if (mAdapter != null) {
            mAdapter.replaceAll(datas);
            mAdapter.notifyDataSetChanged();
        }
    }
}
