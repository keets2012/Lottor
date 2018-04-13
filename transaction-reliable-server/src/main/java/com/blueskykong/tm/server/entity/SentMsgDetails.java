package com.blueskykong.tm.server.entity;

import lombok.Data;

/**
 * @author keets
 * @data 2018/4/13.
 */
@Data
public class SentMsgDetails {

    private Long totalMsgs;

    private Long rollbackNum;

    private Long preConfirmNums;

    private Long completeNums;

}
