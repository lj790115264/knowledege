package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-05-08 15:39
 */
@Document(indexName = "article_node", type = "article_node")
@Data
public class ArticleKNode {
    @Id
    private String id;
    private String articleId;
    private Long nodeId;
}
