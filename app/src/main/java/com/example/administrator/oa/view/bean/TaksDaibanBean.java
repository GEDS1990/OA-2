package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/25.
 */
public class TaksDaibanBean {
    /**
     * assignee : 1
     * assigneeDisplayName : 临远
     * createTime : 2017-05-06 13:56:52
     * id : 632928853000192
     * name : 填写表单
     * presentationSubject : 收文流程-临远-2017-05-06
     */

    private String assignee;
    private String assigneeDisplayName;
    private String createTime;
    private String id;
    private String name;
    private String presentationSubject;
    // 流程定义实例ID
    private String processId;
    // 表单code，如果是回退后重新编辑，则跳入编辑界面
    private String formCode;

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeDisplayName() {
        return assigneeDisplayName;
    }

    public void setAssigneeDisplayName(String assigneeDisplayName) {
        this.assigneeDisplayName = assigneeDisplayName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresentationSubject() {
        return presentationSubject;
    }

    public void setPresentationSubject(String presentationSubject) {
        this.presentationSubject = presentationSubject;
    }
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }
}
