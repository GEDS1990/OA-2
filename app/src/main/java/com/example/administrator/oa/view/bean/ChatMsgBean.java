package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/10.
 */

public class ChatMsgBean {
    int msgType;
    String msgContent;
    String msgDate;

    public ChatMsgBean(int msgType, String msgContent) {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }
}
