package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.constant.CommonConstant;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TransactionMsgAdapter;
import com.blueskykong.tm.common.enums.ConsumedStatus;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.DateUtils;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.entity.TxTransactionItemAdapter;
import com.blueskykong.tm.server.service.OutputFactoryService;
import com.blueskykong.tm.server.service.TxManagerService;
import com.mongodb.WriteResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@SuppressWarnings("unchecked")
public class TxManagerServiceImpl implements TxManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerServiceImpl.class);

    private final MongoTemplate mongoTemplate;

    private final OutputFactoryService outputFactoryService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    public TxManagerServiceImpl(MongoTemplate mongoTemplate, OutputFactoryService outputFactoryService) {
        this.mongoTemplate = mongoTemplate;
        this.outputFactoryService = outputFactoryService;
    }

    /**
     * 保存事务组 在事务发起方的时候进行调用
     *
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     */
    @Override
    public Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
        try {
            final String groupId = txTransactionGroup.getId();

            final TxTransactionItem item = txTransactionGroup.getItem();
            if (Objects.nonNull(item)) {
                item.setTxGroupId(groupId);
                mongoTemplate.insert(item, CollectionNameEnum.TxTransactionItem.name());
            }
        } catch (Exception e) {
            LogUtil.error(LOGGER, "failed to save TxTransactionGroup, groupId is {} and cause is {}",
                    () -> txTransactionGroup.getId(), e::getLocalizedMessage);
            return false;
        }
        return true;
    }

    /**
     * 更新事务状态
     *
     * @param key     redis key 也就是txGroupId
     * @param taskKey 也就是taskKey
     * @param status  事务状态
     * @param message 执行结果信息
     * @return true 成功 false 失败
     */
    @Override
    public Boolean updateTxTransactionItemStatus(String key, String taskKey, int status, Object message) {
        try {
            Query query = new Query();
            query.addCriteria(new Criteria("txGroupId").is(key)).fields().include("createDate").include("msgs");
            TxTransactionItem item = mongoTemplate.findOne(query, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
            List<TransactionMsgAdapter> msgs = item.getMsgs();
            Boolean success = true;
            if (status == TransactionStatusEnum.COMMIT.getCode()) {
                success = sendTxTransactionMsg(key, msgs);
            }
            if (success) {
                Update update = Update.update("status", status);
                if (Objects.nonNull(message)) {
                    update.set("message", message);
                }
                //计算耗时
                final String createDate = item.getCreateDate();

                final LocalDateTime now = LocalDateTime.now();

                try {
                    final LocalDateTime createDateTime = DateUtils.parseLocalDateTime(createDate);
                    final long consumeTime = DateUtils.getSecondsBetween(createDateTime, now);
                    update.set("consumeTime", consumeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final WriteResult writeResult = mongoTemplate.updateFirst(query, update, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
                return writeResult.getN() > 0;
            }
        } catch (Exception e) {
            LogUtil.error(LOGGER, "failed to send msgs and  groupId is {}, cause is {}", () -> key, e::getMessage);
        }
        return false;
    }


    private Boolean sendTxTransactionMsg(String groupId, List<TransactionMsgAdapter> msgs) {
        try {
            if (CollectionUtils.isNotEmpty(msgs)) {

                msgs.forEach(msg -> {
                    if (msg != null) {
                        msg.setGroupId(groupId);
                        outputFactoryService.sendMsg(msg);
                        mongoTemplate.insert(msg, CollectionNameEnum.TransactionMsg.name());
                        LogUtil.debug(LOGGER, () -> "success send msg");
                    }
                });
            }
        } catch (Exception e) {
            LogUtil.error(LOGGER, "send msgs failure and groupId id {}", () -> groupId);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新 TM中的消息状态
     *
     * @param transactionMsg
     */
    @Override
    public Boolean updateTxTransactionMsgStatus(TransactionMsg transactionMsg) {
        try {
            Query query = new Query();
            query.addCriteria(new Criteria("groupId").is(transactionMsg.getGroupId()).and("subTaskId").is(transactionMsg.getSubTaskId()));
            Update update = Update.update("consumed", transactionMsg.getConsumed());
            String message = transactionMsg.getMessage();
            if (StringUtils.isNotBlank(message)) {
                update.set("message", message);
            }
            final WriteResult writeResult = mongoTemplate.updateFirst(query, update, TransactionMsg.class, CollectionNameEnum.TransactionMsg.name());
            return writeResult.getN() > 0;
        } catch (Exception e) {
            //TODO 处理异常
            LogUtil.error(LOGGER, e::getLocalizedMessage);
            return false;
        }
    }

    /**
     * 往事务组添加事务
     *
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功 false 失败
     */
    @Override
    public Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem) {
        try {
//            redisTemplate.opsForHash().put(cacheKey(txGroupId), txTransactionItem.getTaskKey(), txTransactionItem);
            txTransactionItem.setTxGroupId(txGroupId);
            mongoTemplate.insert(txTransactionItem, CollectionNameEnum.TxTransactionItem.name());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据事务组id 获取所有的子项目
     *
     * @param txGroupId 事务组id
     * @return List<TxTransactionItem>
     */
    @Override
    public List<TxTransactionItem> listByTxGroupId(String txGroupId) {
        Query query = new Query();
        query.addCriteria(new Criteria("txGroupId").is(txGroupId));
        List<TxTransactionItem> items = mongoTemplate.find(query, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());

        return new ArrayList<>(items);
    }

    /**
     * 删除事务组信息  当回滚的时候 或者事务组完全提交的时候
     *
     * @param txGroupId txGroupId 事务组id
     */
    @Override
    public void removeItemsByTxGroupId(String txGroupId) {
        mongoTemplate.remove(new Query().addCriteria(new Criteria("txGroupId").is(txGroupId)), CollectionNameEnum.TxTransactionItem.name());
    }

    @Override
    public int findTxTransactionGroupStatus(String txGroupId) {
        try {
            Query query = new Query();
            query.addCriteria(new Criteria("txGroupId").is(txGroupId));
            TxTransactionItemAdapter itemAdapter = mongoTemplate.findOne(query, TxTransactionItemAdapter.class, CollectionNameEnum.TxTransactionItem.name());
            return itemAdapter.getStatus();
        } catch (BeansException e) {
            e.printStackTrace();
            return TransactionStatusEnum.ROLLBACK.getCode();
        }
    }

    @Override
    public Boolean removeCommitTxGroup() {
        return true;
    }

    @Override
    public List<TxTransactionItem> listTxItemByDelay(Long delay) {

        Timestamp current = new Timestamp(System.currentTimeMillis());
        Timestamp ddl = new Timestamp(current.getTime() - delay * 60 * 1000);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String toStr = sdf.format(ddl);
        Criteria criteria = Criteria.where("createDate").lte(toStr).and("status").is(TransactionStatusEnum.PRE_COMMIT.getCode());
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, TxTransactionItem.class, CollectionNameEnum.TxTransactionItem.name());
    }

    @Override
    public List<TransactionMsgAdapter> listTxMsgByDelay(Long delay) {
        Long current = System.currentTimeMillis();
        Long ddl = current - delay * 60 * 1000;
        Criteria criteria = Criteria.where("createTime").lte(ddl).and("consumed").is(ConsumedStatus.UNCONSUMED.getStatus());
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, TransactionMsgAdapter.class, CollectionNameEnum.TransactionMsg.name());
    }

    /**
     * 删除回滚的事务组
     *
     * @return true 成功  false 失败
     */
    @Override
    public Boolean removeRollBackTxGroup() {
        return true;
    }

    private String cacheKey(String key) {
        return String.format(CommonConstant.REDIS_PRE_FIX, key);
    }
}
