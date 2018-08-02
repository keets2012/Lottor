package com.blueskykong.lottor.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInvocation implements Serializable {
    private static final long serialVersionUID = 7722060715819141844L;

    @Getter
    private Class targetClazz;

    @Getter
    private String method;

    @Getter
    private Object[] argumentValues;

    @Getter
    private Class[] argumentTypes;


}
