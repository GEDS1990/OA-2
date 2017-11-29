package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class ProcessShenheHistoryRes {

    /**
     * code : 200
     * data : [{"assignee":"1","completeTime":"02017-04-27 17:31:33"},{"assignee":"620375081484288","comment":"同意","completeTime":"02017-04-27 17:31:33"},{"assignee":"620375081484288","comment":"同意","completeTime":"02017-04-27 17:32:59"},{"assignee":"5","comment":"同意","completeTime":"02017-04-27 17:34:38"},{"assignee":"26","comment":"办结","completeTime":"02017-04-27 17:35:30"},{"assignee":"10","comment":"办结","completeTime":"02017-04-27 17:35:30"},{"assignee":"1","completeTime":"02017-04-27 17:35:30"}]
     */

    private int code;
    private List<ProcessShenheHistoryBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ProcessShenheHistoryBean> getData() {
        return data;
    }

    public void setData(List<ProcessShenheHistoryBean> data) {
        this.data = data;
    }

}
