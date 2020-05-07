package com.brain.java.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-05-03 14:40
 */
@Data
public class IncludeRelationListResponse {

    List<AntvNode> nodes = new ArrayList<>();

    List<AntvEdges> edges = new ArrayList<>();

}
