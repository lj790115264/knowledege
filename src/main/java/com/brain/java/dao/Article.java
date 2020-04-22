package com.brain.java.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @program: java
 * @description:
 * @author: liyx
 * @create: 2020-04-22 15:20
 */
@Document(indexName = "article", type = "article")
@Data
public class Article {
    @Id
    private Long id;
    private String title;
    private String url;
}
