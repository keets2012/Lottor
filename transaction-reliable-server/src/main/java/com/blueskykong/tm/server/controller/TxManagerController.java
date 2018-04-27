package com.blueskykong.tm.server.controller;

import com.blueskykong.tm.common.entity.TxManagerServer;
import com.blueskykong.tm.common.entity.TxManagerServiceDTO;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.entity.ChannelInfo;
import com.blueskykong.tm.server.entity.TxManagerInfo;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import com.blueskykong.tm.server.service.execute.HttpTransactionExecutor;
import com.blueskykong.tm.server.socket.SocketManager;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPipeline;
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
import java.util.ArrayList;
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

    @GetMapping
    public List<ChannelInfo> getChannel() {
        List<Channel> channels = SocketManager.getInstance().getClients();
        List<ChannelInfo> addrs = new ArrayList<>(2);
        channels.stream().forEach(channel -> {
            DefaultChannelPipeline channelPipeline = (DefaultChannelPipeline) channel.pipeline();
            System.out.println(channel.pipeline().toString());
            ChannelInfo channelInfo = new ChannelInfo();
            channelInfo.setClient(channel.remoteAddress().toString());
            channelInfo.setServer(channel.localAddress().toString());
            addrs.add(channelInfo);
        });
        return addrs;
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
    public TxManagerInfo findTxManagerInfo() {
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

    @GetMapping("/analysis")
    public Map<String, Long> totalMsgs() {
        return txManagerInfoService.totalMsgs();
    }

    @GetMapping("/cluster-info")
    public List<TxManagerInfo> getTxManagerDetails() {
        return txManagerInfoService.findClusterInfo();
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
