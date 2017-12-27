package com.example.administrator.oa.view.bean;

/**
 * Created by Administrator on 2017/7/4.
 */

public class VersionResponse {

    /**
     * code : 200
     * data : {"sessionId":"1d6bb471-f2a0-4943-9d78-c5be02432087","userName":"anonymousUser"}
     */

    private int code;
    /**
     * path 更新包url地址，只有code返回201时json里才有path这个键值对
     * version 最新版本号
     */

    private String path;
    private String version;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
