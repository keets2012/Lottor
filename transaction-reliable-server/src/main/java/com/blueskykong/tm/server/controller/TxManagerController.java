package com.blueskykong.tm.server.controller;

import com.blueskykong.tm.common.entity.TxManagerServer;
import com.blueskykong.tm.common.entity.TxManagerServiceDTO;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.entity.TxManagerInfo;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import com.blueskykong.tm.server.service.execute.HttpTransactionExecutor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tx/manager")
public class TxManagerController {

    private final TxManagerInfoService txManagerInfoService;

    private final HttpTransactionExecutor httpTransactionExecutor;

    @Autowired
    public TxManagerController(TxManagerInfoService txManagerInfoService, HttpTransactionExecutor transactionExecutor) {
        this.txManagerInfoService = txManagerInfoService;
        this.httpTransactionExecutor = transactionExecutor;
    }

    @ResponseBody
    @PostMapping("/findTxManagerServer")
    public TxManagerServer findTxManagerServer() {
        return txManagerInfoService.findTxManagerServer();
    }

    @ResponseBody
    @PostMapping("/loadTxManagerService")
    public List<TxManagerServiceDTO> loadTxManagerService() {
        return txManagerInfoService.loadTxManagerService();
    }

    @GetMapping("/findTxManagerInfo")
    public List<TxManagerInfo> findTxManagerInfo() {
        return txManagerInfoService.findTxManagerInfo();
    }

    @PostMapping("/tx-count")
    public Map<String, String> getTxCount(@RequestParam int limit, @RequestParam String date) {
        Timestamp timestamp = Timestamp.valueOf(date);
        Map<String, String> res;
        res = txManagerInfoService.txCountByDate(timestamp, limit);
        return res;
    }

    @GetMapping("/clients-analysis")
    public Map<String, String> getClients(@RequestParam(defaultValue = "true", required = false) Boolean source) {

        return txManagerInfoService.clientDetails(source);
    }

    /**
     * 消费补偿相关，待完善
     */
    @PostMapping("/httpCommit")
    public void httpCommit(@RequestBody List<TxTransactionItem> items) {
        httpTransactionExecutor.commit(items);
    }

    //TODO
    @PostMapping("/httpRollBack")
    public void httpRollBack(@RequestBody List<TxTransactionItem> items) {
        httpTransactionExecutor.rollBack(items);
    }

}
