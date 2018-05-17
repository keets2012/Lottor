package com.blueskykong.sc.producer.service.impl;

import com.blueskykong.sc.producer.domain.Product;
import com.blueskykong.sc.producer.service.PayService;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TxTransactionMsg;
import com.blueskykong.tm.common.enums.MethodNameEnum;
import com.blueskykong.tm.common.enums.ServiceNameEnum;
import com.blueskykong.tm.common.exception.TransactionException;
import com.blueskykong.tm.common.holder.IdWorkerUtils;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.serizlize.kryo.KryoSerialize;
import com.blueskykong.tm.common.serializer.KryoSerializer;
import com.blueskykong.tm.core.service.ExternalNettyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author keets
 * @data 2018/3/19.
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private ExternalNettyService nettyService;

    private KryoSerializer kryoSerialize = new KryoSerializer();

    private static final Logger LOGGER = LoggerFactory.getLogger(PayServiceImpl.class);

    @Override
    public Boolean createAffair() {
        TransactionMsg transactionMsg = new TransactionMsg();
        transactionMsg.setSource(ServiceNameEnum.TEST_PRODUCER.getServiceName());
        transactionMsg.setTarget(ServiceNameEnum.TEST.getServiceName());
        //TODO 下个版本优化，客户端暂时需要序列化对象
        try {
//            Product product = new Product("123", "apple", "an apple a day");
            Map<String, String> arg = new HashMap<>();
            arg.put("123", "456");
            transactionMsg.setArgs(kryoSerialize.serialize(arg));
        } catch (TransactionException e) {
            LogUtil.error(LOGGER, "failed to serialize: {} for: {} ", () -> Product.class.toString(), e::getLocalizedMessage);
            throw new IllegalArgumentException("illegal params to serialize!");
        }

        transactionMsg.setMethod(MethodNameEnum.CONSUMER_TEST.getMethod());
        transactionMsg.setSubTaskId(IdWorkerUtils.getInstance().createUUID());
        TxTransactionMsg txTransactionMsg = new TxTransactionMsg();
        txTransactionMsg.setMsgs(Collections.singletonList(transactionMsg));
        nettyService.preSend(txTransactionMsg);

        try {
            LogUtil.debug(LOGGER, () -> "执行本地事务！");
/*            if (Objects.nonNull(transactionMsg)) {
                throw new IllegalArgumentException("check your parameter!");
            }*/
            int i = 2;
            int j = i / 1;
        } catch (Exception e) {
            nettyService.postSend(false, e.getMessage());
            LogUtil.error(LOGGER, () -> "执行本地事务失败！");
            return false;
        }
        nettyService.postSend(true, null);
        return true;
    }
}
