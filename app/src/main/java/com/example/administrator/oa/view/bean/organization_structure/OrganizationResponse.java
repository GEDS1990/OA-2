package com.example.administrator.oa.view.bean.organization_structure;

import java.util.List;

/**
 * Created by Administrator on 2017/7/14.
 */

public class OrganizationResponse {
    private int code;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }
}
