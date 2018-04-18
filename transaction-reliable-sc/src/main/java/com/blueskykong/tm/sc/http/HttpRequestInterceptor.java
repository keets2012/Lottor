package com.blueskykong.tm.sc.http;

import com.blueskykong.tm.common.constant.CommonConstant;
import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionLocal;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author keets
 */
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(CommonConstant.TX_TRANSACTION_GROUP, TxTransactionLocal.getInstance().getTxGroupId());
        return execution.execute(request, body);
    }
}
