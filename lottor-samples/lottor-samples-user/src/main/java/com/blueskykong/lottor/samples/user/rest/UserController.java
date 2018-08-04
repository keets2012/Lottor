package com.blueskykong.lottor.samples.user.rest;

import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.samples.user.domain.StateEnum;
import com.blueskykong.lottor.samples.user.domain.UserEntity;
import com.blueskykong.lottor.samples.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * @data 2018/3/19.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public String user(@RequestParam(value = "result", required = false) String result) {

        String userName = getRandomString(30);
        if (StringUtils.isBlank(result))
            result = "success";
        switch (result) {
            case "success":
            default:
                result = "success";
                userService.createUser(new UserEntity(UUID.randomUUID().toString(), userName, "pwd", "test"), StateEnum.PRODUCE_SUCCESS);
                break;
            case "fail":
                userService.createUser(new UserEntity(UUID.randomUUID().toString(), userName, "pwd", "test"), StateEnum.PRODUCE_FAIL);
                break;
            case "consumeFail":
                result = "consumeFail";
                userService.createUser(new UserEntity(UUID.randomUUID().toString(), userName, "pwd", "test"), StateEnum.CONSUME_FAIL);
                break;
        }
        return result;
    }

    private String getRandomString(int length) {
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
