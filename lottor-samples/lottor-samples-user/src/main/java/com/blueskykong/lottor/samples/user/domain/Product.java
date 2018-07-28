package com.blueskykong.lottor.samples.user.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @data 2018/5/2.
 */
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;

    private String id;

    private String name;

    private String description;

    public Product(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
