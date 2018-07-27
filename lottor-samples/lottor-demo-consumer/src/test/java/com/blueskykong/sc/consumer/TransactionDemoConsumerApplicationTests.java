package com.blueskykong.sc.consumer;

import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Consumer;
import java.util.function.Function;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionDemoConsumerApplicationTests {

    @Test
    public void contextLoads() {
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        System.out.println(backToString.apply("123").getClass());
        Function t = (p) -> p;
        People people = new People("1", "123");

        Object o = people;

        People product = new People("1", "2");
        String name = product.getClass().getName();

        System.out.println("class type is: " + name);
        Class clzz = null;
        try {
            clzz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

//        Object classObj = clzz.newInstance();
        System.out.println(clzz.getName());
        Consumer f = (p) -> {
            System.out.println(p);
        };

        Consumer<People> greeter = (p) -> System.out.println("Hello, " + p.getName());
    }

    @AllArgsConstructor
    static class People {

        String name;

        String desc;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
