package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: liyx
 * @create: 2020-04-22 16:33
 */
@Document(indexName = "node", type = "node")
@Data
public class KNode {
    private Long id;
    private String name;
}
