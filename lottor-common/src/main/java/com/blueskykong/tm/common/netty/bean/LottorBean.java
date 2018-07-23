package com.blueskykong.tm.common.netty.bean;

import java.util.Arrays;

/**
 * <pre>
 * 自己定义的协议
 *  数据包格式
 * +——----——+——-----——+——----——+
 * |协议开始标志|  长度             |   数据       |
 * +——----——+——-----——+——----——+
 * 1.协议开始标志head_data，为int类型的数据，16进制表示为0X76
 * 2.传输数据的长度contentLength，int类型
 * 3.要传输的数据,长度不应该超过2048，防止socket流的攻击
 * </pre>
 *
 *
 * @data 2018/6/9.
 */
public class LottorBean {
    /**
     * 消息的开头的信息标志
     */
    private int head_data = 0X76;
    /**
     * 消息的长度
     */
    private int contentLength;
    /**
     * 消息的内容
     */
    private byte[] content;

    /**
     * 用于初始化，LottorBean
     *
     * @param contentLength 协议里面，消息数据的长度
     * @param content       协议里面，消息的数据
     */
    public LottorBean(int contentLength, byte[] content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public int getHead_data() {
        return head_data;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LottorBean [head_data=" + head_data + ", contentLength="
                + contentLength + ", content=" + Arrays.toString(content) + "]";
    }

}
