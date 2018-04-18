package com.blueskykong.tm.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author keets
 * @data 2018/4/26.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfo {

    private String client;

    private String server;

    private String serialProtocol;

    private String metaInfo;

    public ChannelInfo(String client, String server) {
        this.client = client;
        this.server = server;
    }
}
