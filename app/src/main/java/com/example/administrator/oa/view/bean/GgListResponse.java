package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */

public class GgListResponse {

    /**
     * code : 200
     * message : null
     * data : [{"articleId":676927574867968,"content":"4234242343","title":"242"},{"articleId":656807164100608,"content":"dfdsfsdfsf","title":"yhjg"},{"articleId":635521953398784,"content":"央视网消息：发展成就载；漫漫丝路，泽遗百代。沉寂了数百年后，这条古老的丝绸之路正重新焕发出勃勃生机。\u201c一带一路\u201d不仅凝聚了各国各地区人民的共同命运，也必将铸就人类和平与发展的美好未来！　(文/程祥)","title":"大道行共赢 习近平指挥\u201c一带一路\u201d交响乐之华彩篇"},{"articleId":637347170992128,"content":"没办法  ","title":"测试一下  能看到的晚上就不用加班了"}]
     */

    private int code;
    private Object message;
    private List<GonggoBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public List<GonggoBean> getData() {
        return data;
    }

    public void setData(List<GonggoBean> data) {
        this.data = data;
    }

}
