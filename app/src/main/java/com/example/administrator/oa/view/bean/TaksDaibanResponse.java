package com.example.administrator.oa.view.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 */

public class TaksDaibanResponse {

    /**
     * code : 200
     * data : [{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-05-06 13:56:52","id":"632928853000192","name":"填写表单","presentationSubject":"收文流程-临远-2017-05-06"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-05-06 13:43:05","id":"632915295207424","name":"发起人","presentationSubject":"用印申请流程-临远-2017-05-06"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-05-05 11:44:16","id":"631382922199040","name":"退回发起人","presentationSubject":"用印申请流程-临远-2017-05-05"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-28 16:03:56","id":"621729147699200","name":"通知发起人","presentationSubject":"用车申请-临远-2017-04-28 16:02"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-28 12:56:08","id":"621544531656704","name":"通知发起人","presentationSubject":"用车申请-临远-2017-04-28 10:52"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-27 17:50:34","id":"620418387869696","name":"通知发起人","presentationSubject":"新闻发布-临远-2017-04-26 20:51"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-27 17:35:30","id":"620403580796928","name":"发起人确认","presentationSubject":"请假流程-临远-2017-04-27 17:31"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-27 16:57:44","id":"620366453112832","name":"发起人确认","presentationSubject":"出差申请-临远-2017-04-27 16:56"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-27 16:53:07","id":"620361909190656","name":"发起人确认","presentationSubject":"出差申请-临远-2017-04-27 16:47"},{"assignee":"1","assigneeDisplayName":"临远","createTime":"2017-04-27 15:48:39","id":"620298536402944","name":"退回发起人","presentationSubject":"出差申请-临远-2017-04-27 15:46"}]
     */

    private int code;
    private List<TaksDaibanBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<TaksDaibanBean> getData() {
        return data;
    }

    public void setData(List<TaksDaibanBean> data) {
        this.data = data;
    }

}
