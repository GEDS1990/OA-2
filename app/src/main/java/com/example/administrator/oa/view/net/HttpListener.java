package com.example.administrator.oa.view.net;

import com.yanzhenjie.nohttp.rest.Response;

/**
 * Created by Administrator on 2017/9/29.
 */

public interface HttpListener<T> {

    void onSucceed(int what, Response<T> response);

    void onFailed(int what, Response<T> response);
}
