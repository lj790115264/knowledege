package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-23 16:14
 */
@Document(indexName = "relation", type = "relation")
@Data
public class Relation {
    @Id
    private Long id;
    private String name;
    private String type;
}
