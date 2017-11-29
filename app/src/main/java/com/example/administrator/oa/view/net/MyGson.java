package com.example.administrator.oa.view.net;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/6/29.
 */

public enum MyGson {
    GSON;
    private Gson mGson;

    MyGson() {
        mGson = new Gson();
    }

    public Gson getGson() {
        return mGson;
    }
}
