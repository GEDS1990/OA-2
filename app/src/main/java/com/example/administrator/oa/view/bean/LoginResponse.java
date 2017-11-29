package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/4.
 */

public class LoginResponse {

    /**
     * code : 200
     * data : {"sessionId":"1d6bb471-f2a0-4943-9d78-c5be02432087","userName":"anonymousUser"}
     */

    private int code;
    private LoginBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LoginBean getData() {
        return data;
    }

    public void setData(LoginBean data) {
        this.data = data;
    }

}
