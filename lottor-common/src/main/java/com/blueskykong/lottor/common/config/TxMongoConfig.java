package com.blueskykong.lottor.common.config;

import lombok.Data;

@Data
public class TxMongoConfig {

    private String mongoDbName;

    private String mongoDbUrl;

    private String mongoUserName;

    private String mongoUserPwd;

}
