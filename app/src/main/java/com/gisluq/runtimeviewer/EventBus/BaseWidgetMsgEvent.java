package com.gisluq.runtimeviewer.EventBus;

/**
 * Widget系统消息事件
 * Created by luq on 2017/5/14.
 */

public class BaseWidgetMsgEvent {
    private String message;

    public BaseWidgetMsgEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
