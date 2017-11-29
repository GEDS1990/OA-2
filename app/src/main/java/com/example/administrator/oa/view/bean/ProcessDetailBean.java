package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/3.
 */
public class ProcessDetailBean {
    /**
     * name : 请假流程
     * processDefinitionId : 流程的ID
     */

    private String name;
    private String processDefinitionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
}
