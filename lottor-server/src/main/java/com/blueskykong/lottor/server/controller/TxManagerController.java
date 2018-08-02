package com.blueskykong.lottor.server.controller;

import com.blueskykong.lottor.common.entity.TxManagerServer;
import com.blueskykong.lottor.common.entity.TxManagerServiceDTO;
import com.blueskykong.lottor.server.entity.ChannelInfo;
import com.blueskykong.lottor.server.entity.TxManagerInfo;
import com.blueskykong.lottor.server.service.TxManagerInfoService;
import com.blueskykong.lottor.server.socket.SocketManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tx/manager")
public class TxManagerController {

    private final TxManagerInfoService txManagerInfoService;

    @Autowired
    public TxManagerController(TxManagerInfoService txManagerInfoService) {
        this.txManagerInfoService = txManagerInfoService;
    }

    @GetMapping
    public List<ChannelInfo> getChannel() {
        List<ChannelInfo> addrs = SocketManager.getInstance().getChannelInfos();
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
}
