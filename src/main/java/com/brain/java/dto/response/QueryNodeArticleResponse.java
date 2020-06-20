package com.brain.java.dto.response;

import com.brain.java.dao.Article;
import lombok.Data;

import java.util.List;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-23 15:31
 */
@Data
public class QueryNodeArticleResponse {
    private List<Article> articles;
}
