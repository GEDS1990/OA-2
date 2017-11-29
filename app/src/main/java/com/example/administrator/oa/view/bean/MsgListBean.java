package com.example.administrator.oa.view.bean;

import com.example.administrator.oa.view.net.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */

public class MsgListBean extends BaseResponse<List<MsgListBean>> {
    /**
     * id : 641458344050688
     * time : 2017-05-12 14:33:31
     * senderUsername : bot
     * content : <p>
     * 您负责的任务固定资产领用-徐徐-2017-05-12即将过期。 <a href="http://localhost/NJSmartOA/operation/task-operation-viewTaskForm.do?humanTaskId=641458343591936">查看</a></p>
     */

    private String id;
    private String time;
    private String senderUsername;
    private String content;
    // 消息状态 0未读；1已读
    private String status;
    // 消息未读件数，放在列表的最后一个
    private String count;

    public MsgListBean(String id, String time, String senderUsername, String content, String status, String count) {
        this.id = id;
        this.time = time;
        this.senderUsername = senderUsername;
        this.content = content;
        this.status = status;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
