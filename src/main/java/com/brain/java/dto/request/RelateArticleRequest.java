package com.brain.java.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-22 12:00
 */
@Data
public class RelateArticleRequest {

    private String articleId;
    private Long nodeId;

}
