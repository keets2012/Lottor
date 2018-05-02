package com.blueskykong.tm.common.enums;

/**
 * @author DengrongGuan
 * @create 2018-05-02
 **/
public enum MethodNameEnum {
    MODIFY_AFFAIR("modifyAffair");

    String methodName;

    MethodNameEnum(String methodName){
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
