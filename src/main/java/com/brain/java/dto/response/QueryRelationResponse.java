package com.brain.java.dto.response;

import com.brain.java.dao.Article;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-23 15:43
 */
@Data
@Builder
public class QueryRelationResponse {

    private String relation;
    private Long relationId;
    private String relationRemark;
    private List<QueryRelationNode> list;
    private List<Article> articles;
}
