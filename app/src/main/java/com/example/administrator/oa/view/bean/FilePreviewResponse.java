package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public class FilePreviewResponse {

    /**
     * code : 200
     * data :
     */

    private int code;
    private FilePreviewBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public FilePreviewBean getData() {
        return data;
    }

    public void setData(FilePreviewBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
