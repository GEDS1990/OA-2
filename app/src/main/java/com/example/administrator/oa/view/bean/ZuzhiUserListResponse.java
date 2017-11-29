package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public class ZuzhiUserListResponse {

    /**
     * code : 200
     * data : [{"id":"8","name":"robot"}]
     */

    private int code;
    private List<ZuzhiUserBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ZuzhiUserBean> getData() {
        return data;
    }

    public void setData(List<ZuzhiUserBean> data) {
        this.data = data;
    }

}
