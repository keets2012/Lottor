package com.blueskykong.lottor.sc.service;


import com.blueskykong.lottor.core.service.ModelNameService;
import org.springframework.beans.factory.annotation.Value;


public class SpringCloudModelNameServiceImpl implements ModelNameService {

    @Value("${spring.application.name}")
    private String modelName;

    @Value("${spring.cloud.consul.discovery.instance-id}")
    private String metaInfo;


    @Override
    public String findModelName() {
        return modelName;
    }

    /**
     * 获取netty 客户端的元数据信息
     *
     * @return
     */
    @Override
    public String findClientMetaInfo() {
        return this.metaInfo;
    }
}
