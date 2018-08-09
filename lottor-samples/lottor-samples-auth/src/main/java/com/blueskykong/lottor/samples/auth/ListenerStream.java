package com.blueskykong.lottor.samples.auth;

import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.enums.MethodNameEnum;
import com.blueskykong.lottor.common.serializer.ObjectSerializer;
import com.blueskykong.lottor.core.service.ExternalNettyService;
import com.blueskykong.lottor.samples.auth.domain.RoleEntity;
import com.blueskykong.lottor.samples.auth.domain.UserRole;
import com.blueskykong.lottor.samples.auth.service.RoleUserService;
import com.blueskykong.lottor.samples.auth.stream.TestSink;
import com.blueskykong.lottor.samples.common.UserRoleDTO;
import com.blueskykong.lottor.sc.service.InitStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
@EnableBinding({TestSink.class})
public class ListenerStream extends InitStreamHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerStream.class);

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    public ListenerStream(ExternalNettyService nettyService, ObjectSerializer objectSerializer) {
        super(nettyService, objectSerializer);
    }


    @StreamListener(TestSink.INPUT)
    public void processSMS(Message message) {
        process(init(message));
    }

    @Transactional
    public void process(TransactionMsg message) {
        try {
            if (Objects.nonNull(message)) {
                LOGGER.info("===============consume notification message: =======================" + message.toString());
                if (StringUtils.isNotBlank(message.getMethod())) {
                    MethodNameEnum method = MethodNameEnum.fromString(message.getMethod());
                    LOGGER.info(message.getMethod());
                    switch (method) {
                        case AUTH_ROLE:
                            UserRoleDTO userRoleDTO = (UserRoleDTO) message.getArgs();
                            RoleEntity roleEntity = roleUserService.getRole(userRoleDTO.getRoleEnum().getName());
                            String roleId = "";
                            if (Objects.nonNull(roleEntity)) {
                                roleId = roleEntity.getId();
                            }
                            roleUserService.saveRoleUser(new UserRole(UUID.randomUUID().toString(), userRoleDTO.getUserId(), roleId));
                            LOGGER.info("matched case {}", MethodNameEnum.AUTH_ROLE);
                            break;
                        default:
                            LOGGER.warn("no matched consumer case!");
                            message.setMessage("no matched consumer case!");
                            nettyService.consumedSend(message, false);
                            return;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            message.setMessage(e.getLocalizedMessage());
            nettyService.consumedSend(message, false);
            return;
        }
        nettyService.consumedSend(message, true);
        return;
    }
}