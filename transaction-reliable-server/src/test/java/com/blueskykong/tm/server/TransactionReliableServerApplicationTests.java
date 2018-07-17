package com.blueskykong.tm.server;

import com.blueskykong.tm.server.entity.ChannelInfo;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import net.sf.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionReliableServerApplicationTests {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void contextLoads() {
        ChannelInfo info = new ChannelInfo();
        info.setServer("123");
        JSONObject jsonObject = JSONObject.fromObject(info);
        System.out.println(jsonObject.toString());
        mongoTemplate.save(jsonObject.toString(),"testjson");

        Query query = new Query();
        query.addCriteria(new Criteria("server").is("123"));
        ChannelInfo f = mongoTemplate.findOne(query, ChannelInfo.class,"testjson");
        System.out.println(f);
    }


    static String str = "modifyAffair";

    public static void main(String[] args) {
        switch (str) {
            case "modifyAffair":
                System.out.println("ok");
                break;
        }
    }
}
