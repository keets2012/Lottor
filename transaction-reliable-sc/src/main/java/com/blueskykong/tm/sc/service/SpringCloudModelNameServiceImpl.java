
package com.blueskykong.tm.sc.service;


import com.blueskykong.tm.core.service.ModelNameService;
import org.springframework.beans.factory.annotation.Value;


/**
 * @author keets
 */
public class SpringCloudModelNameServiceImpl implements ModelNameService {

    @Value("${spring.application.name}")
    private String modelName;


    @Override
    public String findModelName() {
        return modelName;
    }
}
