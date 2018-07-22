package com.blueskykong.tm.server.config;


public class Address {
    private static final Address OUR_INSTANCE = new Address();

    /**
     * 自身的ip
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;


    /**
     * 域名  ip：port
     */
    private String domain;


    public static Address getInstance() {
        return OUR_INSTANCE;
    }

    private Address() {
    }


    public String getHost() {
        return host;
    }

    public Address setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public Address setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Address setDomain(String domain) {
        this.domain = domain;
        return this;
    }
}
