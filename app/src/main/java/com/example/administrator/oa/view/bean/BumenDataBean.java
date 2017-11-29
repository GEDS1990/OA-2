package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */
public class BumenDataBean {
    /**
     * bumen : 企业服务部
     * content
     */

    private String bumen;
    private boolean status;//是否是展开的状态
    private List<ContactsInfoBean> content;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getBumen() {
        return bumen;
    }

    public void setBumen(String bumen) {
        this.bumen = bumen;
    }

    public List<ContactsInfoBean> getContent() {
        return content;
    }

    public void setContent(List<ContactsInfoBean> content) {
        this.content = content;
    }

}
