package com.blueskykong.tm.core.service.impl;

import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.constant.CommonConstant;
import com.blueskykong.tm.common.entity.TxManagerServer;
import com.blueskykong.tm.common.entity.TxManagerServiceDTO;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.holder.httpclient.OkHttpTools;
import com.blueskykong.tm.core.concurrent.threadpool.TxTransactionThreadFactory;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author keets
 */
public class TxManagerLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerLocator.class);

    private static final TxManagerLocator TX_MANAGER_LOCATOR = new TxManagerLocator();

    public static TxManagerLocator getInstance() {
        return TX_MANAGER_LOCATOR;
    }

    private TxConfig txConfig;

    private ScheduledExecutorService mExecutorservice;

    @Setter
    private DiscoveryClient discoveryClient;

    private AtomicReference<List<TxManagerServiceDTO>> mConfigservices;

    private Type mResponsetype;

    public void setTxConfig(TxConfig txConfig) {
        this.txConfig = txConfig;
    }

    private TxManagerLocator() {
        List<TxManagerServiceDTO> initial = Lists.newArrayList();
        mConfigservices = new AtomicReference<>(initial);
        mResponsetype = new TypeToken<List<TxManagerServiceDTO>>() {
        }.getType();
        this.mExecutorservice = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("TxManagerLocator", true));
    }

    //todo 逻辑优化

    /**
     * 获取TxManager 服务信息
     *
     * @return TxManagerServer
     */
    public TxManagerServer locator() {
        int maxRetries = 2;
        final List<TxManagerServiceDTO> txManagerService = getTxManagerService();
        if (CollectionUtils.isEmpty(txManagerService)) {
            return null;
        }
        for (int i = 0; i < maxRetries; i++) {
            List<TxManagerServiceDTO> randomServices = Lists.newLinkedList(txManagerService);
            Collections.shuffle(randomServices);
            for (TxManagerServiceDTO serviceDTO : randomServices) {
                String url = String.join("", serviceDTO.getHomepageUrl(), CommonConstant.TX_MANAGER_PRE, CommonConstant.FIND_SERVER);
                LOGGER.debug("Loading service from {}", url);
                try {
                    return OkHttpTools.getInstance().get(url, null, TxManagerServer.class);
                } catch (Throwable ex) {
                    LogUtil.error(LOGGER, "loadTxManagerServer fail exception:{}", ex::getMessage);
                }
            }
        }
        return null;

    }


    private List<TxManagerServiceDTO> getTxManagerService() {
        if (mConfigservices.get().isEmpty()) {
            updateTxManagerServices();
        }
        return mConfigservices.get();
    }


    public void schedulePeriodicRefresh() {
        this.mExecutorservice.scheduleAtFixedRate(
                () -> {
                    LogUtil.info(LOGGER, "refresh updateTxManagerServices delayTime:{}", () -> txConfig.getRefreshInterval());
                    updateTxManagerServices();
                }, 0, txConfig.getRefreshInterval(),
                TimeUnit.SECONDS);
    }


    private synchronized void updateTxManagerServices() {
        String url = assembleUrl();
        int maxRetries = 2;
        for (int i = 0; i < maxRetries; i++) {
            try {
                final List<TxManagerServiceDTO> serviceDTOList =
                        OkHttpTools.getInstance().get(url, mResponsetype);
                if (CollectionUtils.isEmpty(serviceDTOList)) {
                    LogUtil.error(LOGGER, "Empty response! 请求url为:{}", () -> url);
                    continue;
                }
                mConfigservices.set(serviceDTOList);
                return;
            } catch (Throwable e) {
                LogUtil.error(LOGGER, "updateTxManagerServices fail exception:{}", e::getMessage);
               /* throw new TransactionRuntimeException(
                        String.format("Get config services failed from %s", url), ex);*/
            }
        }
    }

    private String assembleUrl() {
        List<ServiceInstance> managerInstances = discoveryClient.getInstances(txConfig.getTxManagerId());
        Collections.shuffle(managerInstances);
        return managerInstances.stream().map(manager -> {
            String tmpUrl = "http://" + manager.getHost() + ":" + manager.getPort();
            return String.join("", tmpUrl, CommonConstant.TX_MANAGER_PRE, CommonConstant.LOAD_TX_MANAGER_SERVICE_URL);
        }).filter(Objects::nonNull).findFirst().get();
    }

}
