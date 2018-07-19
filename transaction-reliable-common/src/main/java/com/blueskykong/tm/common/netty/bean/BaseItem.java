package com.blueskykong.tm.common.netty.bean;

import lombok.Data;

/**
 * @author keets
 * @data 2018/7/19.
 */
@Data
public class BaseItem {

    private int type;

    private String itemId;

    private boolean healthyState = true;

    private int retryCount = 0;

    private Long lastModified;

    public BaseItem(int type, String itemId) {
        this.type = type;
        this.itemId = itemId;
    }
}
