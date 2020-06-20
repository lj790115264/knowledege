package com.brain.java.dto.request;

import lombok.Data;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-04-26 14:49
 */
@Data
public class QueryNoteRequest {
    private Long id;
    // 1 node 0 relation
    private Integer type;
}
