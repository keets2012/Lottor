package com.blueskykong.tm.server.entity;

import lombok.Data;

/**
 * @author keets
 * @data 2018/4/13.
 */
@Data
public class ConsumeMsgDetails {

    private Long totalMsgs;

    private Long failureNum;

    private Long successNums;

    private Long unConsumeNums;

}
