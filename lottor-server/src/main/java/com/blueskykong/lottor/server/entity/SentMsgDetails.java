package com.blueskykong.lottor.server.entity;

import lombok.Data;

/**
 * @data 2018/4/13.
 */
@Data
public class SentMsgDetails {

    private Long totalMsgs;

    private Long rollbackNum;

    private Long preConfirmNums;

    private Long completeNums;

}
