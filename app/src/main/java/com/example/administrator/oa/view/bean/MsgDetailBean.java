package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/6/29.
 */

public class MsgDetailBean  {


    /**
     * code : 200
     * data : {"content":"您负责的任务审批权限-临远-2017-04-13 09:35即将过期。","data":"600113956339712","id":"600113962024960","senderUsername":"bot","time":"2017-04-13 09:35:50"}
     */

    private int code;
    private MsgDataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MsgDataBean getData() {
        return data;
    }

    public void setData(MsgDataBean data) {
        this.data = data;
    }

}
