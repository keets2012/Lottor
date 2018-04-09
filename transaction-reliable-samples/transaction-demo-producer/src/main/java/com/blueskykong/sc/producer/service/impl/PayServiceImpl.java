package com.blueskykong.sc.producer.service.impl;

import com.blueskykong.sc.producer.service.PayService;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.service.ExternalNettyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * @author keets
 * @data 2018/3/19.
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private ExternalNettyService nettyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PayServiceImpl.class);

    @Override
    public Boolean createAffair() {
        TransactionMsg transactionMsg = new TransactionMsg();
        transactionMsg.setSource("producer");
        transactionMsg.setTarget("affair");
        transactionMsg.setArgs(null);
        transactionMsg.setSubTaskId("4323121");
        nettyService.preSend(Collections.singletonList(transactionMsg));

        try {
            LogUtil.debug(LOGGER, () -> "执行本地事务！");
        } catch (Exception e) {
            nettyService.postSend(false);
            LogUtil.error(LOGGER, () -> "执行本地事务失败！");
            return false;
        }
        nettyService.postSend(true);
        return true;
    }
}
