package com.blueskykong.lottor.core.service;

public interface ModelNameService {

    /**
     * 获取模块名称
     *
     * @return applicationName
     */
    String findModelName();

    /**
     * 获取netty 客户端的元数据信息
     * @return
     */
    String findClientMetaInfo();
}
