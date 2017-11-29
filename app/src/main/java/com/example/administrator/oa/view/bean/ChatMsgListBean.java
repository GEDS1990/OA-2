package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/12.
 */

public class ChatMsgListBean {
    private String time;
    private String name;
    private String latestMsg;
    int picRes;

    public ChatMsgListBean(String time, String name, String latestMsg, int picRes) {
        this.time = time;
        this.name = name;
        this.latestMsg = latestMsg;
        this.picRes = picRes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public int getPicRes() {
        return picRes;
    }

    public void setPicRes(int picRes) {
        this.picRes = picRes;
    }
}
