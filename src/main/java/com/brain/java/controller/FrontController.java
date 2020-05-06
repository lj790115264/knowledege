package com.brain.java.controller;

import com.brain.java.dto.request.IncludeRelationListRequest;
import com.brain.java.dto.response.AntvEdges;
import com.brain.java.dto.response.AntvNode;
import com.brain.java.dto.response.IncludeRelationListResponse;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @program: java
 * @description:
 * @author: lanj
 * @create: 2020-05-03 14:09
 */
@RestController
@CrossOrigin
@RequestMapping("front")
public class FrontController {

    @Autowired
    private Driver driver;

    @PostMapping
    public Object getIncludeRelationList(@RequestBody IncludeRelationListRequest request) {

        String query;
        // 查询包含关系 4度以内
        if (request.getIsInclude()) {
            query  = String.format("match (n) where id(n) = %s \n" +
                    " match p=(n)-[:包含*..4]-() return p", request.getId());

            query = "match (n) match p=(n)-[*..8]-() return p";
        } else {
            // 关联关系 2度以内
            query  = String.format("match (n) where id(n) = %s \n" +
                    " match p=(n)-[*2]-() return p", request.getId());
        }


        Map<String, AntvNode> nodeHashMap = new HashMap<>();
        Map<String, AntvEdges> edgesHashMap = new HashMap<>();
        IncludeRelationListResponse relationListResponse = new IncludeRelationListResponse();
        try (Session session = driver.session()) {
            Result run = session.run(query);
            List<Record> list = run.list();
            for (Record record: list) {
                Path p = record.get("p").asPath();
                Iterator<Node> iterator = p.nodes().iterator();
                while (iterator.hasNext()) {
                    Node next = iterator.next();
                    AntvNode node = new AntvNode();
                    node.setId(next.id() + "");
                    node.setName((String) next.asMap().get("name"));

                    node.setType(next.hasLabel("node") ? "node" : "relation");

                    nodeHashMap.put("node-" + node.getId(), node);
                }
                Iterator<Relationship> iteratorRelation = p.relationships().iterator();
                while (iteratorRelation.hasNext()) {
                    Relationship next = iteratorRelation.next();
                    AntvEdges edges = new AntvEdges();
                    edges.setId(next.id() + "");
                    edges.setSource(next.startNodeId() + "");
                    edges.setTarget(next.endNodeId() + "");
                    edges.setType(next.type());
                    edgesHashMap.put(edges.getId(), edges);
                }
            }
            relationListResponse.setNodes(new ArrayList<>(nodeHashMap.values()));
            relationListResponse.setEdges(new ArrayList<>(edgesHashMap.values()));
            return relationListResponse;
        }
    }
}
