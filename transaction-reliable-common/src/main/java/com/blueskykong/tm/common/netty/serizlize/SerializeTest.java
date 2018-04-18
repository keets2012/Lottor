package com.blueskykong.tm.common.netty.serizlize;


import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.IdWorkerUtils;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.common.netty.serizlize.kryo.KryoPoolFactory;
import com.blueskykong.tm.common.netty.serizlize.kryo.KryoSerialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SerializeTest {

    private static final int MAX = 1000;

    public static void main(String[] args) throws IOException {
        final long start = System.currentTimeMillis();
        for (int i = 0; i < MAX; i++) {
            KryoSerialize kryoSerialization = new KryoSerialize(KryoPoolFactory.getKryoPoolInstance());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            String groupId = IdWorkerUtils.getInstance().createGroupId();
            //创建事务组信息
            TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
            txTransactionGroup.setId(groupId);
            List<TxTransactionItem> items = new ArrayList<>(2);
            //tmManager 用redis hash 结构来存储 整个事务组的状态做为hash结构的第一条数据
            TxTransactionItem groupItem = new TxTransactionItem();
            //整个事务组状态为开始
            groupItem.setStatus(TransactionStatusEnum.BEGIN.getCode());
            //设置事务id为组的id  即为 hashKey
            groupItem.setTransId(groupId);
            groupItem.setTaskKey(groupId);
            items.add(groupItem);
            TxTransactionItem item = new TxTransactionItem();
            item.setTaskKey(IdWorkerUtils.getInstance().createTaskKey());
            item.setTransId(IdWorkerUtils.getInstance().createUUID());
            item.setStatus(TransactionStatusEnum.BEGIN.getCode());
            items.add(item);
            txTransactionGroup.setItemList(items);


            LottorRequest lottorRequest = new LottorRequest();
            lottorRequest.setAction(NettyMessageActionEnum.HEART.getCode());
            lottorRequest.setTxTransactionGroup(txTransactionGroup);

            kryoSerialization.serialize(byteArrayOutputStream, lottorRequest);


            byte[] body = byteArrayOutputStream.toByteArray();


            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

            final LottorRequest lottorRequest1 = (LottorRequest)
                    kryoSerialization.deserialize(byteArrayInputStream);

        }
        final long end = System.currentTimeMillis();

        System.out.println((end - start) / 1000);
    }
}
