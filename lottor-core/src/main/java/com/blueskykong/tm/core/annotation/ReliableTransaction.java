
package com.blueskykong.tm.core.annotation;


import com.blueskykong.tm.common.enums.OperationEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ReliableTransaction {


    OperationEnum value() default OperationEnum.TX_NEW;

    /**
     * 事务等待的最大时间 单位 秒
     *
     * @return 多少秒
     */
    int waitMaxTime() default 60;
}
