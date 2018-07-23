package com.blueskykong.tm.core.compensation.command;


@FunctionalInterface
public interface Command {

    /**
     * 执行命令接口
     *
     * @param txOperateAction 封装命令信息
     */
    void execute(TxOperateAction txOperateAction);
}
