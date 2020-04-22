package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: liyx
 * @create: 2020-04-22 15:23
 */
@Document(indexName = "description", type = "description")
@Data
public class Description {
    @Id
    private Long id;
    private Long articleId;
    private Integer type;
    private Long nodeId;
    private Long associateId;
    private String content;
}

