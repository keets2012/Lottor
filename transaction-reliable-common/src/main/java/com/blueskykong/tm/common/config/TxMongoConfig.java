package com.blueskykong.tm.common.config;

import lombok.Data;

/**
 * @author keets
 */
@Data
public class TxMongoConfig {

    private String mongoDbName;

    private String mongoDbUrl;

    private String mongoUserName;

    private String mongoUserPwd;

}
