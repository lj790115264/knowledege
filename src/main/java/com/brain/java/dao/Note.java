package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-22 15:23
 */
@Document(indexName = "note", type = "note")
@Data
public class Note {

    @Id
    private Long id;
    private Long articleId;
    // 1 node 0 relation
    private Integer type;
    private Long nodeId;
    private String content;
}

