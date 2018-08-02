package com.blueskykong.lottor.server.service;


import com.blueskykong.lottor.common.entity.TxManagerServer;
import com.blueskykong.lottor.common.entity.TxManagerServiceDTO;
import com.blueskykong.lottor.server.entity.TxManagerInfo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface TxManagerInfoService {

    /**
     * 业务端获取TxManager信息
     *
     * @return TxManagerServer
     */
    TxManagerServer findTxManagerServer();

    /**
     * 服务端信息
     *
     * @return TxManagerInfo
     */
    TxManagerInfo findTxManagerInfo();

    /**
     * 获取eureka上的注册服务
     *
     * @return List<TxManagerServiceDTO>
     */
    List<TxManagerServiceDTO> loadTxManagerService();

    /**
     * 获取集群信息
     *
     * @return
     */
    public List<TxManagerInfo> findClusterInfo();

    /**
     * 按照日期获取tx的数量
     *
     * @param timestamp
     * @param limit
     * @return Map
     */
    Map<String, String> txCountByDate(Timestamp timestamp, int limit);

    /**
     * client details
     *
     * @return Map
     */
    Map<String, String> clientDetails(Boolean source);

    /**
     * total msgs
     *
     * @return
     */
    Map<String, Long> totalMsgs();
}
