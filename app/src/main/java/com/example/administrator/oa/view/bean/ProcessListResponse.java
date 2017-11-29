package com.example.administrator.oa.view.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 * 流程定义的类型的列表
 */

public class ProcessListResponse {

    private int code;
    private List<ProcessTypeListBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ProcessTypeListBean> getData() {
        return data;
    }

    public void setData(List<ProcessTypeListBean> data) {
        this.data = data;
    }

}
