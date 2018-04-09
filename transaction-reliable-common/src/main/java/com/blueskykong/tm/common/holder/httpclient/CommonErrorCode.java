package com.blueskykong.tm.common.holder.httpclient;


import org.apache.commons.lang3.StringUtils;

/**
 * 错误码定义类
 * 所有错误码使用10进制进行定义,0-20为系统全局保留使用,
 * 其它各子系统分配99个错误码使用.<br/>
 * 错误码组成:由系统编码:+4位顺序号组成:0000+0000共8位
 * 错误码定义范围<br/>
 * 负数为非明确定义错误
 **/
public class CommonErrorCode {

    public static final int ERROR = -2;

    public static final int SUCCESSFUL = 200;

    public static final int PARAMS_ERROR = 10000002;

    public static final String getErrorMsg(int code) {
        //获取错误信息
        String msg = System.getProperty(String.valueOf(code));
        if (StringUtils.isBlank(msg)) {
            return "根据传入的错误码[" + code + "]没有找到相应的错误信息.";
        }
        return msg;
    }
}
