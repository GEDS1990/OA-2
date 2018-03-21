package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * 待办任务数
 * Created by Administrator on 2017/8/3.
 */

public class UndoTaskNumResponse {

    /**
     * code : 200
     */
    private int code;
    private List<UndoTaskBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<UndoTaskBean> getData() {
        return data;
    }

    public void setData(List<UndoTaskBean> data) {
        this.data = data;
    }
}
