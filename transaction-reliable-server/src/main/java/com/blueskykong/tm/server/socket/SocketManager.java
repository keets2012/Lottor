package com.blueskykong.tm.server.socket;

import com.blueskykong.tm.server.entity.ChannelInfo;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class SocketManager {

    /**
     * 最大连接数
     */
    private int maxConnection = 50;

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    /**
     * 当前连接数
     */

    private int nowConnection;

    /**
     * 允许连接请求 true允许 false拒绝
     */
    private volatile boolean allowConnection = true;

    private List<Channel> clients = Lists.newCopyOnWriteArrayList();

    private List<ChannelInfo> channelInfos = new ArrayList<>();

    private static SocketManager manager = new SocketManager();

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        return manager;
    }


    public Channel getChannelByModelName(String name) {
        if (CollectionUtils.isNotEmpty(clients)) {
            final Optional<Channel> first = clients.stream().filter(channel ->
                    Objects.equals(channel.remoteAddress().toString(), name))
                    .findFirst();
            return first.orElse(null);
        }
        return null;
    }

    public void addClient(Channel client) {
        channelInfos = channelInfos.stream().filter(channelInfo -> !channelInfo.getClient().trim().equalsIgnoreCase(client.remoteAddress().toString())).collect(Collectors.toList());
        channelInfos.add(new ChannelInfo(client.remoteAddress().toString(), client.localAddress().toString()));
        nowConnection = channelInfos.size();
        allowConnection = (maxConnection >= nowConnection);
    }

    public void completeClientInfo(Channel client, String metaInfo, String serialProtocol) {
        channelInfos.stream().filter(channelInfo -> channelInfo.getClient().trim().equalsIgnoreCase(client.remoteAddress().toString()))
                .forEach(channelInfo -> {
                    channelInfo.setMetaInfo(metaInfo);
                    channelInfo.setSerialProtocol(serialProtocol);
                });
    }

    public void removeClient(Channel client) {
        channelInfos = channelInfos.stream().filter(channelInfo -> !channelInfo.getClient().trim().equalsIgnoreCase(client.remoteAddress().toString())).collect(Collectors.toList());
        nowConnection = channelInfos.size();
        allowConnection = (maxConnection >= nowConnection);
    }
}
