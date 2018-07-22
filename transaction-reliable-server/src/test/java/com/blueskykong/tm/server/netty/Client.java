package com.blueskykong.tm.server.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;

/**
 *
 * @data 2018/4/19.
 */
public class Client {
    public static void shallowClone() throws Exception {
        Date date = new Date(12356565656L);
        SimpleObject p1 = new SimpleObject("原型对象", date);
        SimpleObject p2 = (SimpleObject) p1.clone();
        System.out.println(p1);
        System.out.println(p1.date);

        date.setTime(36565656562626L);
        System.out.println(p2);
        System.out.println(p2.date);
        System.out.println(p1.date);
    }

    public static void deepClone() throws Exception {
        Date date = new Date(12356565656L);
        SimpleObject p1 = new SimpleObject("原型对象", date);
        SimpleObject p2 = (SimpleObject) p1.deepClone();
        System.out.println(p1);
        System.out.println(p1.date);
        date.setTime(36565656562626L);
        System.out.println(p2);
        System.out.println(p2.date);
        System.out.println(p1.date);

    }

    public static void deepCloneSerialize() throws Exception {
        Date date = new Date(12356565656L);
        SimpleObject p1 = new SimpleObject("原型对象", date);
        //通过序列化反序列化来新建一个对象
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(p1);
        byte[] bytes = bos.toByteArray();

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        SimpleObject p2 = (SimpleObject) ois.readObject();
        System.out.println(p1);
        System.out.println(p1.date);
        date.setTime(36565656562626L);
        System.out.println(p2);
        System.out.println(p2.date);
        System.out.println(p1.date);

    }

    public static void main(String[] args) throws Exception {
        System.out.println("Shallow clone:");
        shallowClone();
        System.out.println("Deep clone:");
        deepClone();
        System.out.println("Deep clone serialize:");
        deepCloneSerialize();
        System.exit(0);
    }

}