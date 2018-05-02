package com.blueskykong.sc.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author keets
 * @data 2018/5/2.
 */
@Data
@AllArgsConstructor
public class Product {
    private String id;

    private String name;

    private String description;
}
