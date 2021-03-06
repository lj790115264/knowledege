package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-22 16:33
 */
@Document(indexName = "node", type = "node")
@Data
public class KNode {
    @Id
    private Long id;
    private String name;
    private String type;
    private String remark;
}
