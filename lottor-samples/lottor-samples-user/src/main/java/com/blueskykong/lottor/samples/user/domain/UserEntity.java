package com.blueskykong.lottor.samples.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author keets
 * @data 2018/8/2.
 */
@Data
@AllArgsConstructor
public class UserEntity {

    private String id;

    private String username;

    private String password;

    private String selfDesc;

}
