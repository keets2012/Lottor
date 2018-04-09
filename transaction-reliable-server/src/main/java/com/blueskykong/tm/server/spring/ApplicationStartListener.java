package com.blueskykong.tm.server.spring;

import com.blueskykong.tm.server.config.Address;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Component
public class ApplicationStartListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {


    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        int port = event.getEmbeddedServletContainer().getPort();
        final String host = getHost();
        Address.getInstance()
                .setHost(host)
                .setPort(port)
                .setDomain(String.join(":", host, String.valueOf(port)));

    }


    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }
}
