package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.entity.TxManagerServer;
import com.blueskykong.tm.common.entity.TxManagerServiceDTO;
import com.blueskykong.tm.server.config.NettyConfig;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.entity.TxManagerInfo;
import com.blueskykong.tm.server.discovery.DiscoveryService;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import com.blueskykong.tm.server.socket.SocketManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO 优化服务发现
@Service("txManagerInfoService")
public class TxManagerInfoServiceImpl implements TxManagerInfoService {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private NettyConfig nettyConfig;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${redisSaveMaxTime}")
    private int redisSaveMaxTime;

    @Value("${transactionWaitMaxTime}")
    private int transactionWaitMaxTime;


    /**
     * 业务端获取TxManager信息
     *
     * @return TxManagerServer
     */
    @Override
    public TxManagerServer findTxManagerServer() {
        final List<ServiceInstance> tmService = findTMService();
        if (CollectionUtils.isNotEmpty(tmService)) {
            final List<TxManagerInfo> txManagerInfos = findTxManagerInfo();

            if (CollectionUtils.isNotEmpty(txManagerInfos)) {
                //获取连接数最多的服务  想要把所有的业务长连接，连接到同一个tm，但是又不能超过最大的连接
                final Optional<TxManagerInfo> txManagerInfoOptional =
                        txManagerInfos.stream().filter(Objects::nonNull)
                                .filter(info -> info.getNowConnection() < info.getMaxConnection())
                                .sorted(Comparator.comparingInt(TxManagerInfo::getNowConnection).reversed())
                                .findFirst();
                if (txManagerInfoOptional.isPresent()) {
                    final TxManagerInfo txManagerInfo = txManagerInfoOptional.get();
                    TxManagerServer txManagerServer = new TxManagerServer();
                    txManagerServer.setHost(txManagerInfo.getIp());
                    txManagerServer.setPort(txManagerInfo.getPort());
                    return txManagerServer;
                }

            }
        }
        return null;
    }

    /**
     * 服务端信息
     *
     * @return TxManagerInfo
     */
    @Override
    public List<TxManagerInfo> findTxManagerInfo() {
        List<TxManagerInfo> txManagerInfos = new ArrayList<>();
        List<ServiceInstance> serviceInstances = findTMService();
        //设置ip为服务发现注册的TxManager ip
        serviceInstances.stream().forEach(serviceInstance -> {
                    TxManagerInfo txManagerInfo = new TxManagerInfo();
                    String ip = serviceInstance.getHost();
                    txManagerInfo.setIp(ip);
                    txManagerInfo.setPort(nettyConfig.getPort());
                    txManagerInfo.setMaxConnection(SocketManager.getInstance().getMaxConnection());
                    txManagerInfo.setNowConnection(SocketManager.getInstance().getNowConnection());
                    txManagerInfo.setTransactionWaitMaxTime(transactionWaitMaxTime);
                    txManagerInfo.setRedisSaveMaxTime(redisSaveMaxTime);
                    txManagerInfo.setClusterInfoList(serviceInstances.stream().map(instance -> instance.getHost()).collect(Collectors.toList()));
                    txManagerInfos.add(txManagerInfo);
                }
        );
        return txManagerInfos;
    }

    /**
     * 获取注册服务
     *
     * @return List<TxManagerServiceDTO>
     */
    @Override
    public List<TxManagerServiceDTO> loadTxManagerService() {
        final List<ServiceInstance> instanceInfoList = discoveryService.getConfigServiceInstances();
        return instanceInfoList.stream().map(instanceInfo -> {
            TxManagerServiceDTO dto = new TxManagerServiceDTO();
            dto.setAppName(instanceInfo.getServiceId());
            dto.setInstanceId(instanceInfo.getServiceId());
            dto.setHomepageUrl("http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort());
            return dto;
        }).collect(Collectors.toList());
    }


    /**
     * 返回在eureka上注册的服务Url
     *
     * @return List<String>
     */
    private List<ServiceInstance> findTMService() {
        final List<ServiceInstance> configServiceInstances = discoveryService.getConfigServiceInstances();
        return configServiceInstances;
//        return configServiceInstances.stream().map(instance -> "http://" + instance.getHost() + ":" + instance.getPort()).collect(Collectors.toList());
    }
}
