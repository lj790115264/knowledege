package com.brain.java.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-22 16:27
 */
@Data
public class AddRelationRequest {

    // 父节点id
    private Long id;
    // 关系id 若不存在，则创建一个
    private Long relationId;
    private String relation;
    private String relationRemark;

    private List<Long> child;
}
