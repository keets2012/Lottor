
package com.blueskykong.tm.common.config;
import lombok.Data;

/**
 * @author keets
 */
@Data
public class TxDbConfig {


    private String driverClassName = "com.mysql.jdbc.Driver";


    private String url;


    private String username;


    private String password;


    private int initialSize = 10;


    private int maxActive = 100;

    private int minIdle = 20;


    private int maxWait = 60000;


    private int timeBetweenEvictionRunsMillis = 60000;


    private int minEvictableIdleTimeMillis = 300000;


    private String validationQuery = " SELECT 1 ";


    private Boolean testOnBorrow = false;


    private Boolean testOnReturn = false;


    private Boolean testWhileIdle = true;

    private Boolean poolPreparedStatements=false;


    private int maxPoolPreparedStatementPerConnectionSize=100;

}
