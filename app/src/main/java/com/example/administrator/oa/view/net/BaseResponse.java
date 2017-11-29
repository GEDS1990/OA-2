package com.example.administrator.oa.view.net;

/**
 * Created by lsh on 2016/11/8.
 */
public class BaseResponse<T> {

    public int returnCode;
    public T data;
    public String returnMsg;

    public BaseResponse() {
    }

    public BaseResponse(T data, int returnCode, String returnMsg) {
        this.data = data;
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public BaseResponse(int returnCode, String returnMsg) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public T getData() {
        return data;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "returnCode:" + returnCode +
                ", data:" + data +
                ", returnMsg:'" + returnMsg + '\'' +
                '}';
    }

    public boolean isSuccess() {
        int code = getReturnCode();
        if (code == 200 ) {
            return true;
        } else {
            return false;
        }
    }
}
