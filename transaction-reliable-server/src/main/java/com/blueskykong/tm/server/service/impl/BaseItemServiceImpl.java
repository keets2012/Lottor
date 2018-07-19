package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.netty.bean.BaseItem;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.service.BaseItemService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

/**
 * @author keets
 * @data 2018/7/19.
 */
public class BaseItemServiceImpl implements BaseItemService {

    private String collectionName = CollectionNameEnum.BaseItem.name();

    private MongoTemplate mongoTemplate;

    public BaseItemServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int retryCount(String id, int type) {


        return 0;
    }

    @Override
    public int updateItem(BaseItem baseItem) {
        Query query = new Query();
        query.addCriteria(new Criteria("type").is(baseItem.getType()).and("itemId").is(baseItem.getItemId()));
        BaseItem item = mongoTemplate.findOne(query, BaseItem.class, CollectionNameEnum.BaseItem.name());
        if (Objects.isNull(item)) {
            baseItem.setHealthyState(false);
            baseItem.setRetryCount(1);
            baseItem.setLastModified(System.currentTimeMillis());
            mongoTemplate.save(baseItem, collectionName);
            return baseItem.getRetryCount();
        }
        int count = item.getRetryCount();
        Update update = Update.update("retryCount", count + 1);
        update.set("lastModified", System.currentTimeMillis());
        if (count < 1) {
            update.set("healthyState", false);
        }
        mongoTemplate.updateFirst(query, update, BaseItem.class, collectionName);
        return count + 1;
    }
}
