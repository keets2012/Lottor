package com.blueskykong.tm.core.feign;

import com.blueskykong.tm.common.entity.TxManagerServer;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author keets
 * @data 2018/7/25.
 */
@FeignClient(name = "lotor")
public interface ManagerClient {

    @PostMapping(value = "/tx/manager/findTxManagerServer", produces = MediaType.APPLICATION_JSON_VALUE)
    TxManagerServer findTxManagerServers();
}
