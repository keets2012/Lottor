package com.blueskykong.lottor.server.entity;

import com.blueskykong.lottor.common.netty.bean.TxTransactionItem;

/**
 * @data 2018/4/3.
 */
public class TxTransactionItemAdapter extends TxTransactionItem {
    String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
