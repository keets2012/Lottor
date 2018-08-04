package com.blueskykong.lottor.samples.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author keets
 * @data 2018/8/2.
 */
@Data
@AllArgsConstructor
public class RoleEntity {

    private String id;

    private String name;

    private Timestamp updateTime;

    private String description;

}
