package com.itheima.bean;

/**
 * @program: pyg
 * @author: Mr.Jiang
 * @create: 2019-09-04 19:31
 * @description: javaBean封装数据
 **/
public class Message {

    //发送条数
    private int count;
    //发送消息
    private String message;
    //发送时间
    private Long timestamp;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "count=" + count +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public void setAll(int count, String message, long timestamp) {

        this.count = count;
        this.message = message;
        this.timestamp = timestamp;
    }
}
