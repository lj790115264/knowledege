package com.brain.java.controller;

import com.brain.java.dao.KNode;
import com.brain.java.dao.repository.KNodeRepository;
import com.brain.java.dto.request.AddNodeRequest;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class Neo4jController {

    @Autowired
    private KNodeRepository kNodeRepository;

    @Autowired
    private Driver driver;

    @PostMapping("node/add")
    public Object addNode(@RequestBody AddNodeRequest request) {

        String name = request.getName();
        try (Session session = driver.session()) {
            String insertQuery = "merge (n:" + name + " {name:'" + name + "'}) return n";
            StatementResult run = session.run(insertQuery);
            List<Record> list = run.list();
            Long id = list.get(0).get("n").asNode().id();
            KNode node = new KNode();
            node.setName(name);
            node.setId(id);
            kNodeRepository.save(node);
        }

        return null;
    }
}
