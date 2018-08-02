package com.blueskykong.lottor.common.bean.adapter;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class MongoAdapter extends TransactionRecoverAdapter implements Serializable {

    private static final long serialVersionUID = 7920817865031921102L;

    private ObjectId id;


}
