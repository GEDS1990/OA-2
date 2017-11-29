package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */

public class ContactsBean {

    private int code;
    private List<BumenDataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<BumenDataBean> getData() {
        return data;
    }

    public void setData(List<BumenDataBean> data) {
        this.data = data;
    }

}
