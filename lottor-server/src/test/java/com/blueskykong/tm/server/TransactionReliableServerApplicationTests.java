package com.blueskykong.tm.server;

import com.blueskykong.tm.common.concurrent.threadpool.TxTransactionThreadFactory;
import com.blueskykong.tm.common.holder.LogUtil;
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        mongoTemplate.save(jsonObject.toString(), "testjson");

        Query query = new Query();
        query.addCriteria(new Criteria("server").is("123"));
        ChannelInfo f = mongoTemplate.findOne(query, ChannelInfo.class, "testjson");
        System.out.println(f);
    }


    static String str = "modifyAffair";

    public static void main(String[] args) {


        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            List<String> list = Arrays.asList("1", "2", "3");

            CompletableFuture[] cfs = list.stream().map(str -> CompletableFuture.runAsync(() -> {
                System.out.println("========" + str);

                if (str.equals("2")) {
                    int i = 1/0;
                    System.out.println("---------");
//                    return;
                }
                System.out.println("++++++++");
            }).exceptionally(e -> {
                System.out.println(e.getMessage());
                return null;
            }).whenComplete((v, e) -> System.out.println(v))).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(cfs).join();
        }, 1, 3, TimeUnit.SECONDS);
    }

    @Test
    public void thenCombine() {
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "world";
        }), (s1, s2) -> s1 + " " + s2).join();
        System.out.println(result);
    }


    @Test
    public void test() {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("CheckService", true));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int i = 1;
            int j = 10;
            if (1 == 1) {
                System.out.println("===");
//                return;
            }

            System.out.println("+++++++");
        }, 10, 10, TimeUnit.SECONDS);
    }


    @Test
    public void testCom() {
        CompletableFuture<String> txManagerServerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            int i = 1/0;
            return "12";
        }).exceptionally(e -> {
            e.printStackTrace();
            return "ee";
        }).whenComplete((v, e) -> {
            System.out.println("vvvvvvvvvvvv + "+v);
            System.out.println("eeeeeeeeeeee + "+e);
        });
        String str = txManagerServerCompletableFuture.join();
        System.out.println("result: "+str);
    }
}
