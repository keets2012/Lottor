package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TxManagerServer;
import com.blueskykong.tm.common.entity.TxManagerServiceDTO;
import com.blueskykong.tm.common.enums.ServiceNameEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.config.NettyConfig;
import com.blueskykong.tm.server.discovery.DiscoveryService;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.entity.TxManagerInfo;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import com.blueskykong.tm.server.socket.SocketManager;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

//TODO 优化服务发现
@Service("txManagerInfoService")
public class TxManagerInfoServiceImpl implements TxManagerInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerInfoServiceImpl.class);

    private DiscoveryService discoveryService;

    private NettyConfig nettyConfig;

    private MongoTemplate mongoTemplate;

    private RestTemplate restTemplate;

    @Value("${redisSaveMaxTime}")
    private int redisSaveMaxTime;

    @Value("${transactionWaitMaxTime}")
    private int transactionWaitMaxTime;

    @Value("${spring.cloud.consul.host}")
    private String localHost;

    public TxManagerInfoServiceImpl(DiscoveryService discoveryService, NettyConfig nettyConfig, MongoTemplate mongoTemplate,
                                    RestTemplate restTemplate) {
        this.discoveryService = discoveryService;
        this.nettyConfig = nettyConfig;
        this.mongoTemplate = mongoTemplate;
        this.restTemplate = restTemplate;
    }

    /**
     * 业务端获取TxManager信息
     *
     * @return TxManagerServer
     */
    @Override
    public TxManagerServer findTxManagerServer() {
        final List<ServiceInstance> tmService = findTMService();
        if (CollectionUtils.isNotEmpty(tmService)) {
            final List<TxManagerInfo> txManagerInfos = findClusterInfo();

            if (CollectionUtils.isNotEmpty(txManagerInfos)) {
                //获取连接数最多的服务  想要把所有的业务长连接，连接到同一个tm，但是又不能超过最大的连接
                final Optional<TxManagerInfo> txManagerInfoOptional =
                        txManagerInfos.stream().filter(Objects::nonNull)
                                .filter(info -> info.getNowConnection() < info.getMaxConnection())
                                .sorted(Comparator.comparingInt(TxManagerInfo::getNowConnection).reversed())
                                .findAny();
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
    public TxManagerInfo findTxManagerInfo() {
        List<ServiceInstance> serviceInstances = findTMService();
        //设置ip为服务发现注册的TxManager ip
        TxManagerInfo txManagerInfo = new TxManagerInfo();
        txManagerInfo.setIp(localHost);
        txManagerInfo.setPort(nettyConfig.getPort());
        txManagerInfo.setMaxConnection(SocketManager.getInstance().getMaxConnection());
        txManagerInfo.setNowConnection(SocketManager.getInstance().getNowConnection());
        txManagerInfo.setTransactionWaitMaxTime(transactionWaitMaxTime);
        txManagerInfo.setRedisSaveMaxTime(redisSaveMaxTime);
        txManagerInfo.setClusterInfoList(serviceInstances.stream().map(instance -> instance.getHost()).collect(Collectors.toList()));
        return txManagerInfo;
    }

    @Override
    public List<TxManagerInfo> findClusterInfo() {
        List<TxManagerInfo> txManagerInfos = new ArrayList<>();
        List<ServiceInstance> serviceInstances = findTMService();

        //设置ip为服务发现注册的TxManager ip
        serviceInstances.stream().filter(Objects::nonNull).forEach(serviceInstance -> {
                    TxManagerInfo txManagerInfo = restTemplate.getForObject("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/tx/manager/findTxManagerInfo", TxManagerInfo.class);
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
     * 按照日期获取tx的数量
     *
     * @param current
     * @param limit
     * @return
     */
    @Override
    public Map<String, String> txCountByDate(Timestamp current, int limit) {
        int oneDayMillis = 24 * 60 * 60 * 1000;
        Map<String, String> res = new HashMap<>();
        for (int i = limit; i-- > 0; ) {
            Timestamp yesterday = new Timestamp(current.getTime() - oneDayMillis);
            res.put("" + current, String.valueOf(countTxMsgs(yesterday, current)));
            current = yesterday;
        }

        return res;
    }

    /**
     * 按照日期获取tx的数量
     *
     * @return
     */
    @Override
    public Map<String, String> clientDetails(Boolean source) {
        Map<String, String> res = new HashMap<>();
        String attr = source ? "source" : "target";
        Arrays.stream(ServiceNameEnum.values()).forEach(serviceNameEnum -> {
            String service = serviceNameEnum.getServiceName();
            Aggregation aggregation = Aggregation.newAggregation(
                    match(new Criteria(attr).is(service)),
                    group(attr).count().as("count")
            );
            AggregationResults<BasicDBObject> results = mongoTemplate.aggregate(aggregation,
                    CollectionNameEnum.TransactionMsg.name(), BasicDBObject.class);
            List<BasicDBObject> dbObjects = results.getMappedResults();
            for (int i = 0; i < dbObjects.size(); i++) {
                BasicDBObject data = dbObjects.get(i);
                res.put(data.getString("_id"), data.getString("count"));
            }
        });

        return res;
    }

    /**
     * total msgs
     *
     * @return
     */
    @Override
    public Map<String, Long> totalMsgs() {
        Map<String, Long> res = new HashMap<>();
        Long items = mongoTemplate.count(new Query(), TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
        res.put("item", items);
        Long msgs = mongoTemplate.count(new Query(), TransactionMsg.class, CollectionNameEnum.TransactionMsg.name());
        res.put("msg", msgs);
        return res;
    }


    private Long countTxMsgs(Timestamp from, Timestamp to) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String toStr = "";
        String fromStr = "";
        try {
            toStr = sdf.format(to);
            fromStr = sdf.format(from);
        } catch (Exception e) {
            LogUtil.error(LOGGER, "日期转换错误，cause is {}", e::getLocalizedMessage);
            throw new IllegalArgumentException("请检查传入的日期参数！");
        }
        Criteria criteria = Criteria.where("createDate").lte(toStr).gte(fromStr);
        Query query = Query.query(criteria);
        return mongoTemplate.count(query, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());

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
