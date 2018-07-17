package com.blueskykong.sc.producer;

import com.blueskykong.sc.producer.domain.Product;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Consumer;
import java.util.function.Function;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionDemoProducerApplicationTests {

    @Test
    public void contextLoads() {
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        backToString.apply("123");
        People p = new People("1", "123");
        Object o = p;
        System.out.println(o.getClass());
    }


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        System.out.println(backToString.apply("123").getClass());
        Function t = (p) -> p;
        People people = new People("1", "123");

        Object o = people;

        Product product = new Product("1", "2", "3");
        String name = product.getClass().getName();

        System.out.println("class type is: " + name);
        Class clzz = Class.forName(name);

//        Object classObj = clzz.newInstance();
        System.out.println(clzz.getName());
        Consumer f = (p) -> {
            System.out.println(p);
        };

        Consumer<People> greeter = (p) -> System.out.println("Hello, " + p.getName());
//        greeter.accept(new People("Luke", "Skywalker"));
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
