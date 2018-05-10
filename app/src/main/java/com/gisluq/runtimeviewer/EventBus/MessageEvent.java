package com.gisluq.runtimeviewer.EventBus;

/**
 * 消息事件
 * Created by luq on 2017/5/14.
 */
public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
