package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/26.
 */
public class ProcessShenheHistoryBean {
    /**
     * assignee : 1
     * completeTime : 02017-04-27 17:31:33
     * comment : 同意
     */

    // 流程节点名称
    private String name;
    // 负责人
    private String assignee;
    // 任务完成时间
    private String completeTime;
    // 任务开始时间
    private String createTime;

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
