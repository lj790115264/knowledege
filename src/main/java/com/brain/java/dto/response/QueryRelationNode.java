package com.brain.java.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-23 17:57
 */
@Data
@Builder
public class QueryRelationNode {

    private Long relationId;
    private Long nodeId;
    private String nodeName;
}