package com.blueskykong.lottor.server.service;

import com.blueskykong.lottor.common.netty.bean.BaseItem;


public interface BaseItemService {

    int retryCount(String id, int type);

    int updateItem(BaseItem baseItem);

}
