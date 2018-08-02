package com.blueskykong.lottor.server.discovery;

import com.blueskykong.lottor.server.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryService.class);

    private DiscoveryClient discoveryClient;

    @Autowired(required = false)
    public DiscoveryService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public List<ServiceInstance> getConfigServiceInstances() {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(Constant.APPLICATION_NAME);
        if (serviceInstances == null) {
            LOGGER.error("获取服务发现失败！");
        }
        return serviceInstances != null ? serviceInstances : new ArrayList<>();
    }
}
