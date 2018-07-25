package com.blueskykong.tm.core.service.handler;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.tm.common.concurrent.threadlocal.TxTransactionTaskLocal;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.compensation.command.TxOperateCommand;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ConfirmTxTransactionHandler implements TxTransactionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmTxTransactionHandler.class);

    private final TxManagerMessageService txManagerMessageService;

    private final TxOperateCommand txOperateCommand;

    public ConfirmTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxOperateCommand txOperateCommand) {

        this.txManagerMessageService = txManagerMessageService;
        this.txOperateCommand = txOperateCommand;
    }

    @Override
    public void handler(TxTransactionInfo info) {

        final String groupId = TxTransactionLocal.getInstance().getTxGroupId();
        LogUtil.info(LOGGER, "tx-transaction confirm, 事务组 id 为：{}", () -> groupId);

        final String waitKey = TxTransactionTaskLocal.getInstance().getTxTaskId();

        try {
            //发起调用
            int status;
            if ((Boolean) info.getArgs()[0] == true) {
                status = TransactionStatusEnum.COMMIT.getCode();
                //确认本地的事务状态，当前措施为删除补偿信息
                txOperateCommand.updateTxCompensation(groupId, TransactionStatusEnum.COMMIT.getCode());
            } else {
                status = TransactionStatusEnum.ROLLBACK.getCode();
            }
            LogUtil.debug(LOGGER, "confirm status: {}", () -> status);

            if (Objects.nonNull(info.getArgs()[1])) {
                final Object exceptionMsg = info.getArgs()[1];
                //通知tm完成事务
                CompletableFuture.runAsync(() ->
                        txManagerMessageService.asyncCompleteCommit(groupId, waitKey, status, exceptionMsg));
            } else {
                CompletableFuture.runAsync(() ->
                        txManagerMessageService.asyncCompleteCommit(groupId, waitKey, status, null));
            }

            LogUtil.info(LOGGER, "tx-transaction end, 事务组 id 为：{}", () -> groupId);
        } catch (final Throwable throwable) {
            //通知tm整个事务组失败，需要回滚标志状态
            //TODO ROLLABCK待优化
            txManagerMessageService.rollBackTxTransaction(groupId, waitKey);
            LogUtil.error(LOGGER, throwable::getLocalizedMessage);
            throw throwable;
        } finally {
            TxTransactionLocal.getInstance().removeTxGroupId();
            TxTransactionTaskLocal.getInstance().removeTxTaskId();
        }
    }
}
