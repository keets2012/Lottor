package com.blueskykong.tm.server.controller;

import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.entity.SentMsgDetails;
import com.blueskykong.tm.server.entity.TxManagerInfo;
import com.blueskykong.tm.server.helper.MongoPageable;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import com.blueskykong.tm.server.service.impl.TxManagerServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/manager")
public class TxManagerIndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerServiceImpl.class);

    private final TxManagerInfoService txManagerInfoService;
    private final int pageSize = 15;
    private MongoTemplate mongoTemplate;

    @Autowired
    public TxManagerIndexController(TxManagerInfoService txManagerInfoService, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.txManagerInfoService = txManagerInfoService;
    }


    @GetMapping("/index")
    public String index(HttpServletRequest request) {
        final List<TxManagerInfo> txManagerInfo = txManagerInfoService.findTxManagerInfo();
        request.setAttribute("info", txManagerInfo);
        return "index";
    }


    @GetMapping("/msg/rollback")
    @ResponseBody
    @ApiOperation("获取所有的回滚消息")
    public List<TxTransactionItem> findRollbackMsgs(@RequestParam(required = false) String sortFiled, @RequestParam(required = false) Boolean desc, @RequestParam(required = false) int pageNum) {
        MongoPageable pageable = new MongoPageable();
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(TransactionStatusEnum.ROLLBACK.getCode()));
        List<Sort.Order> orders = new ArrayList<Sort.Order>();  //排序
        Sort.Direction direction;
        if (desc != null) {
            direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        if (sortFiled != null) {
            orders.add(new Sort.Order(direction, sortFiled));
        } else {
            orders.add(new Sort.Order(direction, "createDate"));
        }
        if ((Object) pageNum != null) {
            LogUtil.debug(LOGGER, "query for rollback msgs, pageNum", () -> pageNum);
            pageable.setPagenumber(pageNum);
        } else {
            pageable.setPagenumber(1);
        }
        pageable.setPagesize(pageSize);
        pageable.setSort(new Sort(orders));
        Long count = mongoTemplate.count(query, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
        // 查询
        List<TxTransactionItem> list = mongoTemplate.find(query.with(pageable), TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
        // 将集合与分页结果封装
        Page<TxTransactionItem> pagelist = new PageImpl<>(list, pageable, count);

        return pagelist.getContent();
    }

    @GetMapping("/msg/count")
    @ResponseBody
    @ApiOperation("获取消息消费的详情")
    public SentMsgDetails sentDetails() {

        SentMsgDetails sentMsgDetails = new SentMsgDetails();
        Criteria criteriaForRollback = Criteria.where("status").is(TransactionStatusEnum.ROLLBACK.getCode());
        Query query = new Query().addCriteria(criteriaForRollback);
        Long rollbackNum = mongoTemplate.count(query, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
        sentMsgDetails.setRollbackNum(rollbackNum);

        Criteria criteriaForPre = Criteria.where("status").is(TransactionStatusEnum.PRE_COMMIT.getCode());
        Long txPre = mongoTemplate.count(new Query().addCriteria(criteriaForPre), TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
        sentMsgDetails.setPreConfirmNums(txPre);
        Criteria criteriaForComplete = Criteria.where("status").is(TransactionStatusEnum.COMMIT.getCode());
        Long complete = mongoTemplate.count(new Query().addCriteria(criteriaForComplete), TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
        sentMsgDetails.setCompleteNums(complete);

        sentMsgDetails.setTotalMsgs(complete + txPre + rollbackNum);

        return sentMsgDetails;
    }
}
