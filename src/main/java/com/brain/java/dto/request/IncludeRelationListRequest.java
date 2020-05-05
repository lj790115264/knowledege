package com.brain.java.dto.request;

import lombok.Data;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-05-03 14:10
 */
@Data
public class IncludeRelationListRequest {
    private Long id;
    // 查询包含关系 4度以内 ，关联关系 2度以内
    private Boolean isInclude = true;
}
