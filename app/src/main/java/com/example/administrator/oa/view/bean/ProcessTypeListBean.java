package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */
public class ProcessTypeListBean {
    /**
     * process : [{"name":"请假流程","processDefinitionId":"leave:12:600333"},
     * {"name":"发文流程","processDefinitionId":"publish:43:600321"},
     * {"name":"企业考核","processDefinitionId":"assessment:12:600305"}]
     * type : 1
     */
    private String department;
    private List<ProcessDetailBean> children;

    public String getType() {
        return department;
    }

    public void setType(String department) {
        this.department = department;
    }

    public List<ProcessDetailBean> getProcess() {
        return children;
    }

    public void setProcess(List<ProcessDetailBean> children) {
        this.children = children;
    }

}
