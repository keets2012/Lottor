package com.blueskykong.tm.server.service;


import com.blueskykong.tm.common.entity.TxManagerServer;
import com.blueskykong.tm.common.entity.TxManagerServiceDTO;
import com.blueskykong.tm.server.entity.TxManagerInfo;

import java.util.List;

/**
 * @author keets
 */
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
    List<TxManagerInfo> findTxManagerInfo();

    /**
     * 获取eureka上的注册服务
     *
     * @return List<TxManagerServiceDTO>
     */
    List<TxManagerServiceDTO> loadTxManagerService();


}
