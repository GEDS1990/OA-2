package com.example.administrator.oa.view.bean;

/**
 * 草稿箱
 * Created by Administrator on 2017/9/19.
 */
public class CaoGaoXiangInfoBean {
    /**
     * processInstancesId : 1515001
     * name : 请假流程-职员1-2017-08-18
     * taskId : 779919726854144
     */

    // 流程表单定义
    private String processDefinitionId;
    // 流程定义实例名称
    private String name;
    // 草稿箱ID
    private String businessKey;
    // 保存时间
    private String createTime;
    // 流程定义实例ID
    private String processId;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
