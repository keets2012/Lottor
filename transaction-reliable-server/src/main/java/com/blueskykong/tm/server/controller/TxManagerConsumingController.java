package com.blueskykong.tm.server.controller;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.ConsumedStatus;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.entity.ConsumeMsgDetails;
import com.blueskykong.tm.server.entity.SentMsgDetails;
import com.blueskykong.tm.server.helper.MongoPageable;
import com.blueskykong.tm.server.service.TxManagerInfoService;
import com.blueskykong.tm.server.service.impl.TxManagerServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author keets
 * @data 2018/4/16.
 */
@RestController
@RequestMapping("/manager/consume")
public class TxManagerConsumingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerConsumingController.class);

    private final TxManagerInfoService txManagerInfoService;
    private final int pageSize = 15;
    private MongoTemplate mongoTemplate;

    @Autowired
    public TxManagerConsumingController(TxManagerInfoService txManagerInfoService, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.txManagerInfoService = txManagerInfoService;
    }


    @GetMapping("/failed")
    @ResponseBody
    @ApiOperation("获取所有的消费失败的消息")
    public List<TransactionMsg> findRollbackMsgs(@RequestParam(required = false) String sortFiled, @RequestParam(required = false) Boolean desc, @RequestParam(required = false) int pageNum) {
        MongoPageable pageable = new MongoPageable();
        Query query = new Query();
        query.addCriteria(Criteria.where("consumed").is(ConsumedStatus.CONSUMED_FAILURE.getStatus()));
        List<Sort.Order> orders = new ArrayList<>();  //排序
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
            LogUtil.debug(LOGGER, "query for fail consume msgs, pageNum is {}", () -> pageNum);
            pageable.setPagenumber(pageNum);
        } else {
            pageable.setPagenumber(1);
        }
        pageable.setPagesize(pageSize);
        pageable.setSort(new Sort(orders));
        Long count = mongoTemplate.count(query, TransactionMsg.class, CollectionNameEnum.TransactionMsg.name());
        // 查询
        List<TransactionMsg> list = mongoTemplate.find(query.with(pageable), TransactionMsg.class, CollectionNameEnum.TransactionMsg.name());
        // 将集合与分页结果封装
        Page<TransactionMsg> pagelist = new PageImpl<>(list, pageable, count);

        return pagelist.getContent();
    }

    @GetMapping("/count")
    @ResponseBody
    @ApiOperation("获取消息消费的详情")
    public ConsumeMsgDetails sentDetails() {

        ConsumeMsgDetails consumeMsgDetails = new ConsumeMsgDetails();
        Criteria criteriaForFailure = Criteria.where("consumed").is(ConsumedStatus.CONSUMED_FAILURE.getStatus());
        Query query = new Query().addCriteria(criteriaForFailure);
        Long failureNum = mongoTemplate.count(query, TxTransactionItem.class, CollectionNameEnum.TransactionMsg.name());
        consumeMsgDetails.setFailureNum(failureNum);

        Criteria criteriaForSuccess = Criteria.where("consumed").is(ConsumedStatus.CONSUMED_SUCCESS.getStatus());
        Long successConsume = mongoTemplate.count(new Query().addCriteria(criteriaForSuccess), TxTransactionItem.class, CollectionNameEnum.TransactionMsg.name());
        consumeMsgDetails.setSuccessNums(successConsume);
        Criteria criteriaForUnConsumed = Criteria.where("consumed").is(ConsumedStatus.UNCONSUMED.getStatus());

        Long unConsume = mongoTemplate.count(new Query().addCriteria(criteriaForUnConsumed), TxTransactionItem.class, CollectionNameEnum.TransactionMsg.name());
        consumeMsgDetails.setUnConsumeNums(unConsume);

        consumeMsgDetails.setTotalMsgs(failureNum + successConsume + unConsume);

        return consumeMsgDetails;
    }


}
