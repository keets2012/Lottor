package com.blueskykong.tm.server.service;

import com.blueskykong.tm.common.netty.bean.BaseItem;

/**
 * @author keets
 * @data 2018/7/19.
 */
public interface BaseItemService {

    int retryCount(String id, int type);

    int updateItem(BaseItem baseItem);

}
