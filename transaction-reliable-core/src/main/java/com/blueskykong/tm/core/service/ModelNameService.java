package com.blueskykong.tm.core.service;

/**
 * @author keets
 */
@FunctionalInterface
public interface ModelNameService {

    /**
     * 获取模块名称
     *
     * @return applicationName
     */
    String findModelName();
}
