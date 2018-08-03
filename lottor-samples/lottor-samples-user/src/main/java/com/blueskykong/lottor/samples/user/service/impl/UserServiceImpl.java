package com.blueskykong.lottor.samples.user.service.impl;

import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.enums.MethodNameEnum;
import com.blueskykong.lottor.common.enums.ServiceNameEnum;
import com.blueskykong.lottor.common.holder.IdWorkerUtils;
import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.core.service.ExternalNettyService;
import com.blueskykong.lottor.samples.user.domain.UserEntity;
import com.blueskykong.lottor.samples.user.service.UserService;
import com.blueskykong.lottor.samples.user.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ExternalNettyService nettyService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean createUser(UserEntity userEntity) {
        int result = userMapper.saveUser(userEntity);
        System.out.println("======" + result);
        return result > 0;
    }

    /*@Override
    @Transactional
    public Boolean createUser(UserEntity userEntity) {
        TransactionMsg transactionMsg = new TransactionMsg();
        transactionMsg.setSource(ServiceNameEnum.TEST_USER.getServiceName());
        transactionMsg.setTarget(ServiceNameEnum.TEST_AUTH.getServiceName());
        Map<String, String> arg = new HashMap<>();
        arg.put("123", "456");
        transactionMsg.setArgs(arg);
//            LogUtil.error(LOGGER, "failed to serialize: {} for: {} ", () -> Product.class.toString(), e::getLocalizedMessage);
//            throw new IllegalArgumentException("illegal params to serialize!");
        transactionMsg.setMethod(MethodNameEnum.CONSUMER_TEST.getMethod());
        transactionMsg.setSubTaskId(IdWorkerUtils.getInstance().createUUID());
        nettyService.preSend(Collections.singletonList(transactionMsg));

        try {
            LogUtil.debug(LOGGER, () -> "执行本地事务！");
            int i = 2;
            int j = i / 0;
        } catch (
                Exception e)

        {
            nettyService.postSend(false, e.getMessage());
            LogUtil.error(LOGGER, () -> "执行本地事务失败！");
            return false;
        }
        nettyService.postSend(true, null);
        return true;
    }*/


}
