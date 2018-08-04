package com.blueskykong.lottor.samples.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author keets
 * @data 2018/8/2.
 */

@Data
@AllArgsConstructor
public class Permission {

    private String id;

    private String permission;

    private String description;

}
