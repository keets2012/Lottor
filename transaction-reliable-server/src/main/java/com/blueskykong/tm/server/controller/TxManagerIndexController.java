package com.blueskykong.tm.server.controller;

import com.blueskykong.tm.server.entity.TxManagerInfo;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
public class TxManagerIndexController {

    private final TxManagerInfoService txManagerInfoService;

    @Autowired
    public TxManagerIndexController(TxManagerInfoService txManagerInfoService) {
        this.txManagerInfoService = txManagerInfoService;
    }


    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        final List<TxManagerInfo> txManagerInfo = txManagerInfoService.findTxManagerInfo();
        request.setAttribute("info", txManagerInfo);
        return "index";
    }


}
