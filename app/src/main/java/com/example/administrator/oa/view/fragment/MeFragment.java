package com.example.administrator.oa.view.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.LoginActivity;
import com.example.administrator.oa.view.activity.MainActivity;
import com.example.administrator.oa.view.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/3/21.
 */
public class MeFragment extends BaseFragment {

//    @BindView(R.id.me_iv_head)
//    TextView mMeIvHead;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.me_head_contaniner)
    RelativeLayout mMeHeadContaniner;
    @BindView(R.id.account)
    TextView mAccount;
    @BindView(R.id.department)
    TextView mDepartment;
    @BindView(R.id.phone)
    TextView mPhone;
    @BindView(R.id.login_out)
    Button mLoginOut;
    Unbinder unbinder;
    private MainActivity mainActivity;
    private String mDepartmentName;
    private String mUserName;
    private String mUserType;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView(View childHeadView, RelativeLayout rlBaseheaderBack, RelativeLayout rlBaseheaderHeader,
                            RelativeLayout rlBaseheaderRight, View childView, LinearLayout myStatusBar) {
        childHeadView.setVisibility(View.GONE);
        myStatusBar.setVisibility(View.GONE);
        mainActivity = (MainActivity) getActivity();
        initThisView();
    }

    private void initThisView() {
        mUserType = SPUtils.getString(mainActivity,"userType");
        mDepartmentName = SPUtils.getString(mainActivity,"departmentName");
        mUserName = SPUtils.getString(mainActivity,"userName");

        mName.setText(mUserName);
        mAccount.setText(SPUtils.getString(mainActivity,"loginName"));
        mDepartment.setText(SPUtils.getString(mainActivity,"departmentName"));
        mPhone.setText(SPUtils.getString(mainActivity,"mobile"));

//        if (!TextUtils.isEmpty(mUserName)) {
//            String firstStr = String.valueOf(mUserName.charAt(0));
//            mMeIvHead.setText(firstStr);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder!=null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.me_head_contaniner, R.id.login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.me_head_contaniner:
                break;
            case R.id.login_out:
                SPUtils.put(mainActivity, "userName", "");
                readyGo(LoginActivity.class);
                mainActivity.finish();
                break;
        }
    }
}
