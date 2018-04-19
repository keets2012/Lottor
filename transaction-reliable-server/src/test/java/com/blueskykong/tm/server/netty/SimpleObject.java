package com.blueskykong.tm.server.netty;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author keets
 * @data 2018/4/19.
 */
public class SimpleObject implements Cloneable, Serializable {
    String name;
    Date date;

    public SimpleObject(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public SimpleObject() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }

    protected Object deepClone() throws CloneNotSupportedException {
        Object obj = this.clone();
        SimpleObject p = (SimpleObject) obj;
        p.date = (Date) this.date.clone();
        return obj;
    }

}
