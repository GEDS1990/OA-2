package com.example.administrator.oa.view.net;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.RestRequest;
import com.yanzhenjie.nohttp.rest.StringRequest;

/**
 * Created by Administrator on 2017/6/29.
 */

public class JavaBeanRequest<T> extends RestRequest<T> {

    private Class<T> clazz;

    public JavaBeanRequest(String url, Class<T> clazz) {
        this(url, RequestMethod.POST, clazz);
    }

    public JavaBeanRequest(String url, RequestMethod requestMethod, Class<T> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }

    @Override
    public T parseResponse(Headers responseHeaders, byte[] responseBody) {
        String response = StringRequest.parseResponseString(responseHeaders, responseBody);
        // 这里如果数据格式错误，或者解析失败，会在失败的回调方法中返回 ParseError 异常。
        return MyGson.GSON.getGson().fromJson(response, clazz);
    }
}
