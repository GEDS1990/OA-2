package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/3.
 */

public class WorkFromTypeResponse {

    /**
     * code : 200
     * data : [{"name":"12"},{"name":"22"},{"name":"33"},{"name":"44"},{"name":"55"},{"name":"66"},{"name":"77"},{"name":"88"},{"name":"gfhf"},{"name":"454"},{"name":"534"},{"name":"测试"},{"name":"ce"}]
     */

    private int code;
    private List<WorkFromTypeBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<WorkFromTypeBean> getData() {
        return data;
    }

    public void setData(List<WorkFromTypeBean> data) {
        this.data = data;
    }

}
