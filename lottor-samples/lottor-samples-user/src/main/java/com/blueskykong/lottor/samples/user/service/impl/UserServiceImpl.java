package com.blueskykong.lottor.samples.user.service.impl;

import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.enums.MethodNameEnum;
import com.blueskykong.lottor.common.enums.ServiceNameEnum;
import com.blueskykong.lottor.common.holder.IdWorkerUtils;
import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.core.service.ExternalNettyService;
import com.blueskykong.lottor.samples.common.RoleEnum;
import com.blueskykong.lottor.samples.common.UserRoleDTO;
import com.blueskykong.lottor.samples.user.domain.StateEnum;
import com.blueskykong.lottor.samples.user.domain.UserEntity;
import com.blueskykong.lottor.samples.user.service.UserService;
import com.blueskykong.lottor.samples.user.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ExternalNettyService nettyService;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Boolean createUser(UserEntity userEntity, StateEnum flag) {
        UserRoleDTO userRoleDTO = new UserRoleDTO(RoleEnum.ADMIN, userEntity.getId());

        TransactionMsg transactionMsg = new TransactionMsg.Builder()
                .setSource(ServiceNameEnum.TEST_USER.getServiceName())
                .setTarget(ServiceNameEnum.TEST_AUTH.getServiceName())
                .setMethod(MethodNameEnum.AUTH_ROLE.getMethod())
                .setSubTaskId(IdWorkerUtils.getInstance().createUUID())
                .setArgs(userRoleDTO)
                .build();

        if (flag == StateEnum.CONSUME_FAIL) {
            userRoleDTO.setUserId(null);
            transactionMsg.setArgs(userRoleDTO);
        }

        //netty预处理
        if (!nettyService.preSend(Collections.singletonList(transactionMsg))) {
            return false;
        }

        //local transaction
        try {
            LogUtil.debug(LOGGER, () -> "执行本地事务！");
            if (flag != StateEnum.PRODUCE_FAIL) {
                userMapper.saveUser(userEntity);
            } else {
                userMapper.saveUserFailure(userEntity);
            }
        } catch (Exception e) {
            nettyService.postSend(false, e.getMessage());
            LogUtil.error(LOGGER, "执行本地事务失败，cause is 【{}】", e::getLocalizedMessage);
            return false;
        }
        nettyService.postSend(true, null);
        return true;
    }

}
